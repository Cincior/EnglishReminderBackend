package pl.cinkus.backend.mapper;


import pl.cinkus.backend.codegen.types.Category;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.entity.WordData;

public class WordDataMapper {
    public static WordDataDTO toDTO(WordData wordData, float memorizeChance) {
        return WordDataDTO.builder()
                .id(wordData.getId().toString())
                .word(wordData.getWord())
                .translation(wordData.getTranslation())
                .insertDateTime(wordData.getInsertDateTime())
                .category(wordData.getCategory())
                .memorizeChance(memorizeChance)
                .build();
    }
}
