package com.andersenlab.assesment.exception.handler;

import com.andersenlab.assesment.exception.AbstractCustomException;
import com.andersenlab.assesment.exception.ResourceAlreadyExistsException;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.exception.model.ErrorResponse;
import com.andersenlab.assesment.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

    @ExceptionHandler({
            ResourceNotFoundException.class,
            ResourceAlreadyExistsException.class
    })
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleCustomException(AbstractCustomException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ErrorResponse.builder()
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .details(ex.getDetails())
                        .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        FieldError fieldError = ex.getBindingResult().getAllErrors().stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .findFirst()
                .orElse(null);
        String fieldName = fieldError != null ? fieldError.getField() : null;
        String message = fieldError != null ? fieldError.getDefaultMessage() : null;
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(ErrorCode.byMessage(ex.getMessage()))
                        .message(message)
                        .details(fieldName)
                        .build()
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(ErrorCode.ERR003)
                        .message(ErrorCode.ERR003.getMessage())
                        .details(ex.getMessage())
                        .build()
                );
    }
}
