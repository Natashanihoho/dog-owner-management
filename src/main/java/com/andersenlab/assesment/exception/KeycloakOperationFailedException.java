package com.andersenlab.assesment.exception;

import com.andersenlab.assesment.exception.model.ErrorCode;
import org.springframework.http.HttpStatus;

public class KeycloakOperationFailedException extends AbstractCustomException {

    public KeycloakOperationFailedException(ErrorCode errorCode, String details, HttpStatus httpStatus) {
        super(errorCode, details, httpStatus);
    }
}
