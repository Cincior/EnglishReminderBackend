package pl.cinkus.IdentityService.validation;

import org.springframework.stereotype.Component;
import pl.cinkus.IdentityService.exception.IdentityServiceException;
import pl.cinkus.IdentityService.exception.ErrorCode;
import pl.cinkus.backend.codegen.types.NewUserData;

import java.util.regex.Pattern;

@Component
public class UserRegistrationValidation {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]{2,50}$");

    private static final Pattern SURNAME_PATTERN = Pattern.compile("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ \\-]{2,50}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$");

    public void validate(NewUserData userData) {
        validateName(userData.getName());
        validateSurname(userData.getSurname());
        validateEmail(userData.getEmail());
        validatePassword(userData.getPassword());
    }

    public void validateName(String name) {
        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            throw new IdentityServiceException(
                    ErrorCode.VALIDATION_ERROR,
                    "Name is not valid"
            );
        }
    }

    public void validateSurname(String surname) {
        if (surname == null || !SURNAME_PATTERN.matcher(surname).matches()) {
            throw new IdentityServiceException(
                    ErrorCode.VALIDATION_ERROR,
                    "Surname is not valid"
            );
        }
    }

    public void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IdentityServiceException(
                    ErrorCode.VALIDATION_ERROR,
                    "Email is not valid"
            );
        }
    }

    public void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IdentityServiceException(
                    ErrorCode.VALIDATION_ERROR,
                    "Password is not valid"
            );
        }
    }
}
