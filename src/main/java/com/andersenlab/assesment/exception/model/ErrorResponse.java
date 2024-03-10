package com.andersenlab.assesment.exception.model;

import lombok.Builder;

@Builder
public record ErrorResponse (ErrorCode errorCode, String message, String details) {
}
