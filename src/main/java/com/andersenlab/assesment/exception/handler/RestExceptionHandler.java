package com.andersenlab.assesment.exception.handler;

import com.andersenlab.assesment.exception.*;
import com.andersenlab.assesment.exception.model.ErrorCode;
import com.andersenlab.assesment.exception.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@RequiredArgsConstructor
public class RestExceptionHandler {

    @ExceptionHandler({
            ResourceNotFoundException.class,
            ResourceAlreadyExistsException.class,
            ActionNotAllowedException.class,
            RegistrationFailedException.class
    })
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleCustomException(AbstractCustomException ex) {
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
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
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
                        .errorCode(ErrorCode.byMessage(message))
                        .message(message)
                        .details(fieldName)
                        .build()
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String fieldName = getFieldNameFromConstraint(ex.getConstraintViolations().iterator().next());
        String errorMessageFromAnnotation = getAnnotationMessageFromConstraint(ex.getConstraintViolations().iterator().next());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(ErrorCode.byMessage(errorMessageFromAnnotation))
                        .message(errorMessageFromAnnotation)
                        .details(fieldName)
                        .build()
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .errorCode(ErrorCode.ERR003)
                        .message(ErrorCode.ERR003.getMessage())
                        .details(ex.getMessage())
                        .build()
                );
    }

    private String getFieldNameFromConstraint(ConstraintViolation<?> violation) {
        String field = null;
        for (Path.Node node : violation.getPropertyPath()) {
            field = node.getName();
        }
        return field;
    }

    private String getAnnotationMessageFromConstraint(ConstraintViolation<?> violation) {
        return violation.getMessage();
    }
}
