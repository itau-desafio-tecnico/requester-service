package com.itau.desafio.requesterservice.interfaces.rest.dto;

import java.util.UUID;

public record ValidationResponse(
        UUID requesterId,
        boolean validation
) {
}
