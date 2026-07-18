package com.itau.desafio.requesterservice.domain.exception;

public class DocumentInvalidException extends RuntimeException {
    public DocumentInvalidException(String message) {
        super(message);
    }
}
