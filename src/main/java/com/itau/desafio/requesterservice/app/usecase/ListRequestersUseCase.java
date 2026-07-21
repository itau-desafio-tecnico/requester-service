package com.itau.desafio.requesterservice.app.usecase;

import com.itau.desafio.requesterservice.domain.model.PagedResult;
import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;

public class ListRequestersUseCase {
    private final RequesterRepository requesterRepository;

    public ListRequestersUseCase(RequesterRepository requesterRepository) {
        this.requesterRepository = requesterRepository;
    }

    public PagedResult<Requester> execute(int page, int size, Boolean active) {
        return requesterRepository.findAll(page, size, active);
    }
}
