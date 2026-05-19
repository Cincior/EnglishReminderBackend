package pl.cinkus.backend.exception;

import lombok.Getter;

@Getter
public class WordServiceException extends RuntimeException{
    private final ErrorCode errorCode;

    public WordServiceException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }
}
