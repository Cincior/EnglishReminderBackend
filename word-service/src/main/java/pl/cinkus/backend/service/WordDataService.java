package pl.cinkus.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cinkus.backend.codegen.types.InputWordData;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.exception.ErrorCode;
import pl.cinkus.backend.exception.WordServiceException;
import pl.cinkus.backend.mapper.WordDataMapper;
import pl.cinkus.backend.model.WordData;
import pl.cinkus.backend.repository.WordDataRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public boolean addWord(InputWordData inputWordData, String ownerId) {
        if(wordDataRepository.existsByWord(inputWordData.getWord()) && !inputWordData.getForceDuplicate()) {
            throw new WordServiceException(ErrorCode.DUPLICATED_WORD, "This word is already saved");
        }

        WordData word = WordData.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .word(inputWordData.getWord())
                .translation(inputWordData.getTranslation())
                .insertDateTime(LocalDateTime.now())
                .build();

        wordDataRepository.save(word);
        return true;
    }
}
