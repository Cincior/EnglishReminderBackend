package pl.cinkus.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cinkus.backend.codegen.types.AddWordResult;
import pl.cinkus.backend.codegen.types.InputWordData;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.exception.ErrorCode;
import pl.cinkus.backend.exception.WordServiceException;
import pl.cinkus.backend.mapper.WordDataMapper;
import pl.cinkus.backend.model.WordData;
import pl.cinkus.backend.repository.WordDataRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WordDataService {
    private final WordDataRepository wordDataRepository;

    public List<WordDataDTO> getUserWords(String ownerId) {
        List<WordData> wordDataList = wordDataRepository.findByOwnerId(ownerId);

        if(wordDataList.isEmpty()) {
            throw new WordServiceException(ErrorCode.NO_WORDS_FOUND, "User does not have any words");
        }

        return wordDataList.stream().map(WordDataMapper::toDTO).toList();
    }

    public AddWordResult addWord(InputWordData inputWordData, String ownerId) {
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

    private AddWordResult saveWord(InputWordData inputWordData, String ownerId) {
        WordData word = WordData.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .word(inputWordData.getWord())
                .translation(inputWordData.getTranslation())
                .insertDateTime(LocalDateTime.now())
                .build();

        WordData savedWord = wordDataRepository.save(word);
        return AddWordResult.newBuilder()
                .created(true)
                .wordId(savedWord.getId().toString())
                .build();
    }
}
