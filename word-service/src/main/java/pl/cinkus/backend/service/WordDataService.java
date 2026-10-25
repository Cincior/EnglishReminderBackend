package pl.cinkus.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cinkus.backend.codegen.types.AddWordResult;
import pl.cinkus.backend.codegen.types.InputWordData;
import pl.cinkus.backend.codegen.types.UpdateWordInput;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.exception.ErrorCode;
import pl.cinkus.backend.exception.WordServiceException;
import pl.cinkus.backend.mapper.WordDataMapper;
import pl.cinkus.backend.entity.WordData;
import pl.cinkus.backend.repository.WordDataRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WordDataService {
    private final WordDataRepository wordDataRepository;

    public List<WordDataDTO> getUserWords(String ownerId) {
        List<WordData> wordDataList = wordDataRepository.findByOwnerId(parseUuid(ownerId, "ownerId"));

        return wordDataList.stream().map(WordDataMapper::toDTO).toList();
    }

    public AddWordResult addWord(InputWordData inputWordData, String ownerId) {
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

    private AddWordResult saveWord(InputWordData inputWordData, String ownerId) {
        WordData word = WordData.builder()
                .id(UUID.randomUUID())
                .ownerId(parseUuid(ownerId, "ownerId"))
                .word(inputWordData.getWord())
                .translation(inputWordData.getTranslation())
                .insertDateTime(LocalDateTime.now())
                .isLearned(false)
                .build();

        WordData savedWord = wordDataRepository.save(word);
        return AddWordResult.newBuilder()
                .created(true)
                .wordId(savedWord.getId().toString())
                .build();
    }

    @Transactional
    public void setLearned(String wordId, String userId, boolean isLearned) {
        WordData wordData = wordDataRepository.findByOwnerIdAndId(parseUuid(userId, "userId"), parseUuid(wordId, "wordId"))
                .orElseThrow(() -> new WordServiceException(ErrorCode.WORD_NOT_FOUND, "We did not find your word!"));

        wordData.setLearned(isLearned);
    }

    @Transactional
    public void updateWord(UpdateWordInput input, String ownerId) {
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
