package com.andersenlab.assesment.exception.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    ERR001(ErrorMessage.ERR001_MESSAGE),
    ERR002(ErrorMessage.ERR002_MESSAGE),
    ERR003(ErrorMessage.ERR003_MESSAGE),
    ERR004(ErrorMessage.ERR004_MESSAGE),
    ERR005(ErrorMessage.ERR005_MESSAGE);

    private final String message;

    public static ErrorCode byMessage(String message) {
        return Arrays.stream(values())
                .filter(err -> err.message.equalsIgnoreCase(message))
                .findAny()
                .orElse(ERR003);
    }
}
