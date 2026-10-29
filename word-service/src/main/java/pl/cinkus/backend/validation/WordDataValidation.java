package pl.cinkus.backend.validation;

import org.springframework.stereotype.Component;
import pl.cinkus.backend.exception.ErrorCode;
import pl.cinkus.backend.exception.WordServiceException;

@Component
public class WordDataValidation {
    private static final int MAX_TEXT_LENGTH = 200;

    public void validate(String word, String translation) {
        validateText(word, "word");
        validateText(translation, "translation");
    }

    public void validateTranslation(String translation) {
        validateText(translation, "translation");
    }

    private void validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new WordServiceException(
                    ErrorCode.INVALID_WORD_DATA,
                    fieldName + " cannot be empty"
            );
        }

        if (value.length() > MAX_TEXT_LENGTH) {
            throw new WordServiceException(
                    ErrorCode.INVALID_WORD_DATA,
                    fieldName + " cannot be longer than " + MAX_TEXT_LENGTH + " characters"
            );
        }
    }
}
