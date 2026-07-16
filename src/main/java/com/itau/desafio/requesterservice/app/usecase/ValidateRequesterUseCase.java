package com.itau.desafio.requesterservice.app.usecase;

import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;

import java.util.UUID;

public class ValidateRequesterUseCase {
    private final RequesterRepository requesterRepository;

    public ValidateRequesterUseCase(RequesterRepository requesterRepository){
        this.requesterRepository = requesterRepository;
    }

    public boolean execute(UUID id){
        return requesterRepository.findById(id).map(Requester::active).orElse(false);
    }
}
