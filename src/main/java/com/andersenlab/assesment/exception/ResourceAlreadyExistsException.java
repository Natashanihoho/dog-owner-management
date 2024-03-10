package com.andersenlab.assesment.exception;

import com.andersenlab.assesment.exception.model.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceAlreadyExistsException extends AbstractCustomException {

    public ResourceAlreadyExistsException(ErrorCode errorCode, String details, HttpStatus httpStatus) {
        super(errorCode, details, httpStatus);
    }
}
