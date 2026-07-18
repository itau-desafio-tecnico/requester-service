package com.itau.desafio.requesterservice.interfaces.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequesterRequest(
        @NotBlank(message = "Document must be provided")
        @Size(min = 11, max = 14, message = "Document must be a valid CPF (11 digits) or CNPJ (14 digits)")
        String document,

        @NotBlank(message = "Name must be provided")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email must be provided")
        @Email(message = "Invalid email")
        String email
) {
}
