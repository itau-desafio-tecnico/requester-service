package com.itau.desafio.requesterservice.domain.exception;

public class RequesterNotFoundException extends RuntimeException {
    public RequesterNotFoundException(String id) {
        super("Requester not found with id: " + id);
    }
}
