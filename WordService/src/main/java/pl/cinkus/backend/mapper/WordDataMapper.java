package pl.cinkus.backend.mapper;


import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.model.WordData;

public class WordDataMapper {
    public static WordDataDTO toDTO(WordData wordData) {
        return WordDataDTO.builder()
                .word(wordData.getWord())
                .translation(wordData.getTranslation())
                .insertDateTime(wordData.getInsertDateTime())
                .build();
    }
}
