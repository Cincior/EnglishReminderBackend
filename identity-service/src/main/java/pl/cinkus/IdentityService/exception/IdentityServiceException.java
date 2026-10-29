package pl.cinkus.IdentityService.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class IdentityServiceException extends RuntimeException{
    private final ErrorCode errorCode;
    private List<ValidationFailedFields> failedFields;

    public IdentityServiceException(ErrorCode errorCode, List<ValidationFailedFields> failedFields, String message) {
        super(message);
        this.errorCode = errorCode;
        this.failedFields = failedFields;
    }

    public IdentityServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
