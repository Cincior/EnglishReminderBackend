package pl.cinkus.backend.mapper;


import pl.cinkus.backend.codegen.types.Category;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.entity.WordData;

public class WordDataMapper {
    public static pl.cinkus.backend.entity.Category toEntityCategory(Category category) {
        return category == null
                ? null
                : pl.cinkus.backend.entity.Category.valueOf(category.name());
    }

    private static Category toGraphqlCategory(pl.cinkus.backend.entity.Category category) {
        return category == null
                ? null
                : Category.valueOf(category.name());
    }

    public static WordDataDTO toDTO(WordData wordData, float memorizeChance) {
        return WordDataDTO.builder()
                .id(wordData.getId().toString())
                .word(wordData.getWord())
                .translation(wordData.getTranslation())
                .insertDateTime(wordData.getInsertDateTime())
                .category(toGraphqlCategory(wordData.getCategory()))
                .memorizeChance(memorizeChance)
                .build();
    }
}
