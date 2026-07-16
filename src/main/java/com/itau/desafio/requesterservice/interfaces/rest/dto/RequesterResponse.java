package com.itau.desafio.requesterservice.interfaces.rest.dto;

import com.itau.desafio.requesterservice.domain.model.Requester;

import java.time.Instant;
import java.util.UUID;

public record RequesterResponse(
        UUID id,
        String document,
        String name,
        String email,
        boolean active,
        Instant createdAt
) {
    public static RequesterResponse from(Requester requester) {
        return new RequesterResponse(
                requester.id(),
                requester.document(),
                requester.name(),
                requester.email(),
                requester.active(),
                requester.createdAt()
        );
    }
}
