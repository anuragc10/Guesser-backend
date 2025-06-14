package com.guesser.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GuesserException extends RuntimeException {
    private final ErrorCodes errorCode;
    private final HttpStatus httpStatus;

    public GuesserException(ErrorCodes errorCode, HttpStatus httpStatus) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public GuesserException(ErrorCodes errorCode, HttpStatus httpStatus, Object... args) {
        super(errorCode.getFormattedMessage(args));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}