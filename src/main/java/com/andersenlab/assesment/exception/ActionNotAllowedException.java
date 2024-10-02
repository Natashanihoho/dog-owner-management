package com.andersenlab.assesment.exception;

import com.andersenlab.assesment.exception.model.ErrorCode;
import org.springframework.http.HttpStatus;

public class ActionNotAllowedException extends AbstractCustomException {

    private static final HttpStatus httpStatus = HttpStatus.FORBIDDEN;

    public ActionNotAllowedException(ErrorCode errorCode, String details) {
        super(errorCode, details, httpStatus);
    }
}
