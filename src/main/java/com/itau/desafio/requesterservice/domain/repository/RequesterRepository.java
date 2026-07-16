package com.itau.desafio.requesterservice.domain.repository;

import com.itau.desafio.requesterservice.domain.model.Requester;

public interface RequesterRepository {
    Requester save(Requester requester);
}
