package pl.cinkus.IdentityService.validation;

import org.springframework.stereotype.Component;
import pl.cinkus.IdentityService.dto.UserDataDTO;
import pl.cinkus.IdentityService.exception.IdentityServiceException;
import pl.cinkus.IdentityService.util.ErrorCode;
import pl.cinkus.backend.codegen.types.UserData;

import java.util.regex.Pattern;

@Component
public class UserRegistrationValidation {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]{2,50}$");

    private static final Pattern SURNAME_PATTERN = Pattern.compile("^[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ \\-]{2,50}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$");

    public void validate(UserData userData) {
        if (userData.getName() == null || !NAME_PATTERN.matcher(userData.getName()).matches()) {
            throw new IdentityServiceException(
                    ErrorCode.VALIDATION_ERROR,
                    "Name is not valid"
            );
        }

        if (userData.getSurname() == null || !SURNAME_PATTERN.matcher(userData.getSurname()).matches()) {
            throw new IdentityServiceException(
                    ErrorCode.VALIDATION_ERROR,
                    "Surname is not valid"
            );
        }

        if (userData.getEmail() == null || !EMAIL_PATTERN.matcher(userData.getEmail()).matches()) {
            throw new IdentityServiceException(
                    ErrorCode.VALIDATION_ERROR,
                    "Email is not valid"
            );
        }

        if (userData.getPassword() == null || !PASSWORD_PATTERN.matcher(userData.getPassword()).matches()) {
            throw new IdentityServiceException(
                    ErrorCode.VALIDATION_ERROR,
                    "Password is not valid"
            );
        }
    }
}
