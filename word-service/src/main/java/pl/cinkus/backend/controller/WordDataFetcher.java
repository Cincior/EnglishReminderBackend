package pl.cinkus.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import pl.cinkus.backend.codegen.types.*;
import pl.cinkus.backend.dto.WordDataDTO;
import pl.cinkus.backend.exception.ErrorCode;
import pl.cinkus.backend.exception.WordServiceException;
import pl.cinkus.backend.service.WordDataService;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WordDataFetcher {
    private final WordDataService wordDataService;

    @PreAuthorize("hasRole('USER')")
    @QueryMapping
    public List<WordDataDTO> getUserWords() {
        return wordDataService.getUserWords(parseUuid(getUserId(), "ownerId"));
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public AddWordResult addWord(@Argument(name = "input") InputWordData inputWordData) {
        return wordDataService.addWord(inputWordData, parseUuid(getUserId(), "ownerId"));
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public WordTestResultPayload testWord(
            @Argument String wordId,
            @Argument String translation
    ) {
        var result = wordDataService.testWord(
                parseUuid(wordId, "wordId"),
                translation,
                parseUuid(getUserId(), "userId")
        );
        return WordTestResultPayload.newBuilder()
                .wordId(result.getWordId().toString())
                .isCorrect(result.isCorrect())
                .testTime(String.valueOf(result.getTestTimeInMilis()))
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public SetCategoryPayload setCategory(@Argument(name = "setCategoryInput") SetCategoryInput setCategoryInput) {
        wordDataService.setCategory(
                parseUuid(setCategoryInput.getWordId(), "wordId"),
                parseUuid(getUserId(), "userId"),
                setCategoryInput.getCategory()
        );
        return SetCategoryPayload.newBuilder()
                .wordId(setCategoryInput.getWordId())
                .category(setCategoryInput.getCategory())
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public UpdateWordPayload updateWord(@Argument(name = "input") UpdateWordInput input) {
        wordDataService.updateWord(
                input,
                parseUuid(getUserId(), "ownerId"),
                parseUuid(input.getWordId(), "wordId")
        );
        return UpdateWordPayload.newBuilder()
                .wordId(input.getWordId())
                .word(input.getWord())
                .translation(input.getTranslation())
                .build();
    }

    @MutationMapping
    @PreAuthorize("hasRole('USER')")
    public DeleteWordPayload deleteWord(@Argument String wordId) {
        wordDataService.deleteWord(
                parseUuid(wordId, "wordId"),
                parseUuid(getUserId(), "ownerId")
        );
        return DeleteWordPayload.newBuilder()
                .deleted(true)
                .wordId(wordId)
                .build();
    }

    @MutationMapping
    public ScheduleNotificationPayload scheduleNotification(@Argument(name = "input") ScheduleNotificationInput notificationInput) {
        wordDataService.scheduleNotification(
                validateScheduleTime(notificationInput.getScheduleTime()),
                parseUuid(getUserId(), "userId"),
                parseUuid(notificationInput.getWordId(), "wordId")
        );
        return ScheduleNotificationPayload.newBuilder()
                .wordId(notificationInput.getWordId())
                .build();
    }

    private String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
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

    private Instant validateScheduleTime(String scheduleTimeString) {
        Instant notificationTime;
        try {
            notificationTime = Instant.parse(scheduleTimeString);
        } catch (DateTimeParseException e) {
            throw new WordServiceException(ErrorCode.WRONG_TIME_FORMAT, "We could not parse a time");
        }

        Instant now = Instant.now();
        if (notificationTime.isBefore(now) || notificationTime.isAfter(now.plus(365, ChronoUnit.DAYS))) {
            throw new WordServiceException(ErrorCode.WRONG_TIME, "Time is not valid");
        }

        return notificationTime;
    }

}
