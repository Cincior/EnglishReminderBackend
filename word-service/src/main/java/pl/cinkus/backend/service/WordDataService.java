package pl.cinkus.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cinkus.backend.client.PredictClient;
import pl.cinkus.backend.client.RabbitMqMessageSender;
import pl.cinkus.backend.codegen.types.*;
import pl.cinkus.backend.dto.PredictRequestData;
import pl.cinkus.backend.dto.PredictResponseData;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.entity.Notification;
import pl.cinkus.backend.entity.NotificationOutbox;
import pl.cinkus.backend.exception.ErrorCode;
import pl.cinkus.backend.exception.WordServiceException;
import pl.cinkus.backend.mapper.WordDataMapper;
import pl.cinkus.backend.entity.WordData;
import pl.cinkus.backend.entity.WordTestResult;
import pl.cinkus.backend.repository.NotificationOutboxRepository;
import pl.cinkus.backend.repository.NotificationRepository;
import pl.cinkus.backend.repository.WordDataRepository;
import pl.cinkus.backend.repository.WordTestResultRepository;
import pl.cinkus.backend.validation.WordDataValidation;
import lombok.extern.slf4j.Slf4j;
import pl.cinkus.messages.SendMobileNotificationMessage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WordDataService {
    private final WordDataRepository wordDataRepository;
    private final WordTestResultRepository wordTestResultRepository;
    private final PredictionFeatureCalculator predictionFeatureCalculator;
    private final PredictClient predictClient;
    private final WordDataValidation validation;
    private final RabbitMqMessageSender rabbitMqMessageSender;
    private final NotificationRepository notificationRepository;
    private final NotificationOutboxRepository notificationOutboxRepository;

    public List<WordDataDTO> getUserWords(UUID ownerId) {
        List<WordData> wordDataList = wordDataRepository.findByOwnerId(ownerId);
        if (wordDataList.isEmpty()) {
            return List.of();
        }

        List<WordTestResult> wordTestResults = wordTestResultRepository.findAllByUserId(ownerId);
        if (wordTestResults.isEmpty()) {
            return wordDataList.stream()
                    .map(word -> WordDataMapper.toDTO(word, -1))
                        .toList();
        }

        log.info("test: " + wordTestResults);
        List<PredictRequestData> requestDataList =
                predictionFeatureCalculator.preparePredictFeatures(wordDataList, wordTestResults);
        log.info("reqData: " + requestDataList.toString());
        if (requestDataList.isEmpty()) {
            return wordDataList.stream()
                    .map(word -> WordDataMapper.toDTO(word, -1))
                    .toList();
        }

        Map<String, Float> probabilityByWordId = predictClient.predict(requestDataList)
                .stream()
                .collect(Collectors.toMap(
                        PredictResponseData::wordId,
                        PredictResponseData::probabilityPercent
                ));
        log.info("resData: " + probabilityByWordId);

        return wordDataList.stream()
                .map(word -> {
                    float probability = probabilityByWordId.getOrDefault(word.getId().toString(), -1.0f);
                    return WordDataMapper.toDTO(word, probability);
                })
                .toList();
    }

    @Transactional
    public AddWordResult addWord(InputWordData inputWordData, UUID ownerId) {
        validation.validate(inputWordData.getWord(), inputWordData.getTranslation());

        if (!inputWordData.getForceDuplicate()) {
            return wordDataRepository.findFirstByOwnerIdAndWord(ownerId, inputWordData.getWord())
                    .map(existingWord -> AddWordResult.newBuilder()
                            .created(false)
                            .existingWordId(existingWord.getId().toString())
                            .build())
                    .orElseGet(() -> saveWord(inputWordData, ownerId));
        }

        return saveWord(inputWordData, ownerId);
    }

    @Transactional
    public WordTestResult testWord(UUID wordId, String translation, UUID userId) {
        validation.validateTranslation(translation);

        WordData wordData = wordDataRepository.findByOwnerIdAndId(userId, wordId)
                .orElseThrow(() -> new WordServiceException(
                        ErrorCode.WORD_NOT_FOUND,
                        "We did not find your word!"
                ));

        boolean isCorrect = wordData.getTranslation().trim().equalsIgnoreCase(translation.trim());
        WordTestResult result = new WordTestResult();
        result.setTestId(UUID.randomUUID());
        result.setWordId(wordId);
        result.setUserId(userId);
        result.setCorrect(isCorrect);
        result.setTestTimeInMilis(Instant.now().toEpochMilli());
        return wordTestResultRepository.save(result);
    }

    private AddWordResult saveWord(InputWordData inputWordData, UUID ownerId) {
        WordData word = WordData.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .word(inputWordData.getWord())
                .translation(inputWordData.getTranslation())
                .category(Category.NOT_LEARNED)
                .build();

        WordData savedWord = wordDataRepository.save(word);
        return AddWordResult.newBuilder()
                .created(true)
                .wordId(savedWord.getId().toString())
                .build();
    }

    @Transactional
    public void setCategory(UUID wordId, UUID userId, Category category) {
        if (category == null) {
            throw new WordServiceException(ErrorCode.CATEGORY_NOT_FOUND, "No such category!");
        }
        WordData wordData = wordDataRepository.findByOwnerIdAndId(userId, wordId)
                .orElseThrow(() -> new WordServiceException(ErrorCode.WORD_NOT_FOUND, "We did not find your word!"));

        wordData.setCategory(category);
    }

    @Transactional
    public void updateWord(UpdateWordInput input, UUID ownerId, UUID wordId) {
        validation.validate(input.getWord(), input.getTranslation());

        WordData wordData = wordDataRepository.findByOwnerIdAndId(ownerId, wordId)
                .orElseThrow(() -> new WordServiceException(
                        ErrorCode.WORD_NOT_FOUND,
                        "We did not find your word!"
                ));

        wordData.setWord(input.getWord());
        wordData.setTranslation(input.getTranslation());
    }

    @Transactional
    public void deleteWord(UUID wordId, UUID ownerId) {
        WordData wordData = wordDataRepository.findByOwnerIdAndId(ownerId, wordId)
                .orElseThrow(() -> new WordServiceException(
                        ErrorCode.WORD_NOT_FOUND,
                        "We did not find your word!"
                ));
        wordDataRepository.delete(wordData);
    }

    @Transactional
    public void scheduleNotification(Instant scheduleTime, UUID userId, UUID wordId) {
        WordData wordData = wordDataRepository.findByOwnerIdAndId(userId, wordId)
                .orElseThrow(() -> new WordServiceException(ErrorCode.WORD_NOT_FOUND, "We did not find your word!"));

        Notification notification = Notification.builder()
                .fcmToken("FCM_TOKEN")
                .word(wordData.getWord())
                .translation(wordData.getTranslation())
                .dateTime(scheduleTime.toString())
                .build();
        notificationRepository.save(notification);
        notificationOutboxRepository.save(
                NotificationOutbox.builder()
                        .notification(notification)
                        .isPublished(false)
                        .isSent(false)
                        .isPublishing(false)
                        .build()
        );

    }

}
