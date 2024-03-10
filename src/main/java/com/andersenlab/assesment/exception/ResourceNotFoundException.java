package com.andersenlab.assesment.exception;

import com.andersenlab.assesment.exception.model.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AbstractCustomException {

    public ResourceNotFoundException(ErrorCode errorCode, String details, HttpStatus httpStatus) {
        super(errorCode, details, httpStatus);
    }
}
