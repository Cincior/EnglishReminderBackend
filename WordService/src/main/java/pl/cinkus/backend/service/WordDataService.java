package pl.cinkus.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cinkus.backend.codegen.types.InputWordData;
import pl.cinkus.backend.dto.WordDataDTO;
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

    public List<WordDataDTO> getUserWords() {
        List<WordData> wordDataList = wordDataRepository.findAll();

        return wordDataList.stream().map(WordDataMapper::toDTO).toList();
    }

    public boolean addWord(InputWordData inputWordData) {
        WordData word = WordData.builder()
                .id(UUID.randomUUID())
                .word(inputWordData.getWord())
                .translation(inputWordData.getTranslation())
                .insertDateTime(LocalDateTime.now())
                .build();

        wordDataRepository.save(word);
        return true;
    }
}
