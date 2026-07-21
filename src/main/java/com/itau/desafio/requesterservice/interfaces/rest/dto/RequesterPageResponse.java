package com.itau.desafio.requesterservice.interfaces.rest.dto;

import com.itau.desafio.requesterservice.domain.model.PagedResult;
import com.itau.desafio.requesterservice.domain.model.Requester;

import java.util.List;

public record RequesterPageResponse(
        List<RequesterResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static RequesterPageResponse from(PagedResult<Requester> pagedResult) {
        List<RequesterResponse> content = pagedResult.content().stream()
                .map(RequesterResponse::from)
                .toList();
        return new RequesterPageResponse(
                content,
                pagedResult.page(),
                pagedResult.size(),
                pagedResult.totalElements(),
                pagedResult.totalPages()
        );
    }
}
