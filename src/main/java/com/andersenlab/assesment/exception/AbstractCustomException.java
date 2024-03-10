package com.andersenlab.assesment.exception;

import com.andersenlab.assesment.exception.model.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AbstractCustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String details;
    private final HttpStatus httpStatus;

    protected AbstractCustomException(ErrorCode errorCode, String details, HttpStatus httpStatus) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
        this.httpStatus = httpStatus;
    }
}
