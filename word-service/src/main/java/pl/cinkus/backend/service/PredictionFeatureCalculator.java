package pl.cinkus.backend.service;

import org.springframework.stereotype.Component;
import pl.cinkus.backend.dto.PredictRequestData;
import pl.cinkus.backend.entity.WordData;
import pl.cinkus.backend.entity.WordTestResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PredictionFeatureCalculator {
    private static final long MAX_LAST_TEST_TIME = 604800L;
    private static final int MIN_TESTS_REQUIRED = 2;

    public List<PredictRequestData> preparePredictFeatures(
            List<WordData> wordDataList,
            List<WordTestResult> wordTestResults
    ) {
        Map<UUID, List<WordTestResult>> resultsByWordId = wordTestResults.stream()
                .collect(Collectors.groupingBy(WordTestResult::getWordId));

        long now = Instant.now().toEpochMilli();
        List<PredictRequestData> requestDataList = new ArrayList<>();

        for (WordData word : wordDataList) {
            List<WordTestResult> wordResults = resultsByWordId.getOrDefault(word.getId(), List.of());

            if (wordResults.size() < MIN_TESTS_REQUIRED) {
                continue;
            }

            requestDataList.add(buildRequestData(word.getId(), wordResults, now));
        }

        return requestDataList;
    }

    private PredictRequestData buildRequestData(UUID wordId, List<WordTestResult> wordResults, long now) {
        long correctCount = 0;
        long wrongCount = 0;
        long maxCorrectTime = -1;
        long maxWrongTime = -1;

        for (WordTestResult result : wordResults) {
            if (result.isCorrect()) {
                correctCount++;
                maxCorrectTime = Math.max(maxCorrectTime, result.getTestTimeInMilis());
            } else {
                wrongCount++;
                maxWrongTime = Math.max(maxWrongTime, result.getTestTimeInMilis());
            }
        }

        long secSinceLastCorrect = maxCorrectTime != -1
                ? (now - maxCorrectTime) / 1000
                : MAX_LAST_TEST_TIME;

        long secSinceLastWrong = maxWrongTime != -1
                ? (now - maxWrongTime) / 1000
                : MAX_LAST_TEST_TIME;

        return PredictRequestData.builder()
                .wordId(wordId.toString())
                .correctCount(correctCount)
                .wrongCount(wrongCount)
                .secSinceLastCorrect(secSinceLastCorrect)
                .secSinceLastWrong(secSinceLastWrong)
                .build();
    }
}