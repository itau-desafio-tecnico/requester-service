package com.itau.desafio.requesterservice.domain.repository;

import com.itau.desafio.requesterservice.domain.model.Requester;

import java.util.Optional;
import java.util.UUID;

public interface RequesterRepository {
    Requester save(Requester requester);

    Optional<Requester> findById(UUID id);

    Optional<Requester> findByDocument(String document);

    boolean existsByDocument(String document);
}
