package com.itau.desafio.requesterservice.app.usecase;

import com.itau.desafio.requesterservice.domain.exception.RequesterNotFoundException;
import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;

import java.util.UUID;

public class GetRequesterUseCase {
    private final RequesterRepository requesterRepository;

    public GetRequesterUseCase(RequesterRepository requesterRepository) {
        this.requesterRepository = requesterRepository;
    }

    public Requester byId(UUID id){
        return requesterRepository.findById(id).orElseThrow(() -> new RequesterNotFoundException(id.toString()));
    }
}
