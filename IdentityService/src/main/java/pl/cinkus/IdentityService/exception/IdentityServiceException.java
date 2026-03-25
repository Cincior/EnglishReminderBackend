package pl.cinkus.IdentityService.exception;

import lombok.Getter;

@Getter
public class IdentityServiceException extends RuntimeException{
    private final ErrorCode errorCode;

    public IdentityServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
