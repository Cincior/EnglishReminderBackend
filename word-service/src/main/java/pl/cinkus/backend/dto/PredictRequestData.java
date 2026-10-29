package pl.cinkus.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record PredictRequestData(
        @JsonProperty("word_id") String wordId,
        @JsonProperty("correct_count") long correctCount,
        @JsonProperty("wrong_count") long wrongCount,
        @JsonProperty("sec_since_last_correct") long secSinceLastCorrect,
        @JsonProperty("sec_since_last_wrong") long secSinceLastWrong
) {}
