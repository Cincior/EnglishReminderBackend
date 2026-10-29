package pl.cinkus.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PredictResponseData(
        @JsonProperty("word_id")
        String wordId,
        int prediction,
        @JsonProperty("probability_percent")
        float probabilityPercent
) {}
