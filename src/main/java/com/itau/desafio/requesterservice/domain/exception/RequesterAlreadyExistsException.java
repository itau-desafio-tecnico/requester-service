package com.itau.desafio.requesterservice.domain.exception;

public class RequesterAlreadyExistsException extends RuntimeException {

    public RequesterAlreadyExistsException(String document) {
        super("Requester with document " + document + " already exists.");
    }
}
