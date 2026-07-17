package com.itau.desafio.requesterservice.interfaces.rest.dto;

import java.time.Instant;

public record ErrorResponse(String code, String message, Instant timestamp) {
    public ErrorResponse(String code, String message) {
        this(code, message, Instant.now());
    }
}
