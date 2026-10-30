package pl.cinkus.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cinkus.backend.client.PredictClient;
import pl.cinkus.backend.codegen.types.AddWordResult;
import pl.cinkus.backend.codegen.types.Category;
import pl.cinkus.backend.codegen.types.InputWordData;
import pl.cinkus.backend.codegen.types.UpdateWordInput;
import pl.cinkus.backend.dto.PredictRequestData;
import pl.cinkus.backend.dto.PredictResponseData;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.exception.ErrorCode;
import pl.cinkus.backend.exception.WordServiceException;
import pl.cinkus.backend.mapper.WordDataMapper;
import pl.cinkus.backend.entity.WordData;
import pl.cinkus.backend.entity.WordTestResult;
import pl.cinkus.backend.repository.WordDataRepository;
import pl.cinkus.backend.repository.WordTestResultRepository;
import pl.cinkus.backend.validation.WordDataValidation;
import lombok.extern.slf4j.Slf4j;
import java.time.Instant;
import java.time.LocalDateTime;
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

    public List<WordDataDTO> getUserWords(String ownerId) {
        UUID ownerIdParsed = parseUuid(ownerId, "ownerId");

        List<WordData> wordDataList = wordDataRepository.findByOwnerId(ownerIdParsed);
        if (wordDataList.isEmpty()) {
            return List.of();
        }

        List<WordTestResult> wordTestResults = wordTestResultRepository.findAllByUserId(ownerIdParsed);
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
    public AddWordResult addWord(InputWordData inputWordData, String ownerId) {
        validation.validate(inputWordData.getWord(), inputWordData.getTranslation());

        if (!inputWordData.getForceDuplicate()) {
            return wordDataRepository.findFirstByOwnerIdAndWord(parseUuid(ownerId, "ownerId"), inputWordData.getWord())
                    .map(existingWord -> AddWordResult.newBuilder()
                            .created(false)
                            .existingWordId(existingWord.getId().toString())
                            .build())
                    .orElseGet(() -> saveWord(inputWordData, ownerId));
        }

        return saveWord(inputWordData, ownerId);
    }

    @Transactional
    public WordTestResult testWord(String wordId, String translation, String userId) {
        UUID parsedUserId = parseUuid(userId, "userId");
        UUID parsedWordId = parseUuid(wordId, "wordId");

        validation.validateTranslation(translation);

        WordData wordData = wordDataRepository.findByOwnerIdAndId(parsedUserId, parsedWordId)
                .orElseThrow(() -> new WordServiceException(
                        ErrorCode.WORD_NOT_FOUND,
                        "We did not find your word!"
                ));

        boolean isCorrect = wordData.getTranslation().trim().equalsIgnoreCase(translation.trim());
        WordTestResult result = new WordTestResult();
        result.setTestId(UUID.randomUUID());
        result.setWordId(parsedWordId);
        result.setUserId(parsedUserId);
        result.setCorrect(isCorrect);
        result.setTestTimeInMilis(Instant.now().toEpochMilli());
        return wordTestResultRepository.save(result);
    }

    private AddWordResult saveWord(InputWordData inputWordData, String ownerId) {
        WordData word = WordData.builder()
                .id(UUID.randomUUID())
                .ownerId(parseUuid(ownerId, "ownerId"))
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
    public void setCategory(String wordId, String userId, Category category) {
        if (category == null) {
            throw new WordServiceException(ErrorCode.CATEGORY_NOT_FOUND, "No such category!");
        }
        WordData wordData = wordDataRepository.findByOwnerIdAndId(parseUuid(userId, "userId"), parseUuid(wordId, "wordId"))
                .orElseThrow(() -> new WordServiceException(ErrorCode.WORD_NOT_FOUND, "We did not find your word!"));

        wordData.setCategory(category);
    }

    @Transactional
    public void updateWord(UpdateWordInput input, String ownerId) {
        validation.validate(input.getWord(), input.getTranslation());

        WordData wordData = wordDataRepository.findByOwnerIdAndId(parseUuid(ownerId, "ownerId"), parseUuid(input.getWordId(), "wordId"))
                .orElseThrow(() -> new WordServiceException(
                        ErrorCode.WORD_NOT_FOUND,
                        "We did not find your word!"
                ));

        wordData.setWord(input.getWord());
        wordData.setTranslation(input.getTranslation());
    }

    @Transactional
    public void deleteWord(String wordId, String ownerId) {
        WordData wordData = wordDataRepository.findByOwnerIdAndId(parseUuid(ownerId, "ownerId"), parseUuid(wordId, "wordId"))
                .orElseThrow(() -> new WordServiceException(
                        ErrorCode.WORD_NOT_FOUND,
                        "We did not find your word!"
                ));
        wordDataRepository.delete(wordData);
    }

    private UUID parseUuid(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new WordServiceException(
                    ErrorCode.INVALID_IDENTIFIER,
                    fieldName + " cannot be empty"
            );
        }

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new WordServiceException(
                    ErrorCode.INVALID_IDENTIFIER,
                    fieldName + " has an invalid format"
            );
        }
    }
}
