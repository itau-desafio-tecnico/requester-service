package com.itau.desafio.requesterservice.interfaces.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequesterRequest(
        @NotBlank(message = "Document must be provided")
        String document,

        @NotBlank(message = "Name must be provided")
        String name,

        @NotBlank(message = "Email must be provided")
        @Email(message = "Invalid email")
        String email
) {
}
