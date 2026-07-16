package com.itau.desafio.requesterservice.app.usecase;

import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;

public class CreateRequesterUseCase {

    private final RequesterRepository requesterRepository;

    public CreateRequesterUseCase(RequesterRepository requesterRepository) {
        this.requesterRepository = requesterRepository;
    }

    public Requester execute(String document, String name, String email){
        Requester requester = Requester.create(document, name, email);
        return requesterRepository.save(requester);
    }
}
