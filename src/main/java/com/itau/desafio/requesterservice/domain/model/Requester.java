package com.itau.desafio.requesterservice.domain.model;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.CPFValidator;
import com.itau.desafio.requesterservice.domain.exception.DocumentInvalidException;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public record Requester(
        UUID id,
        String document,
        String name,
        String email,
        boolean active,
        Instant createdAt
) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^\\d{11}|\\d{14}$");

    public Requester {
        if (id == null) {
            throw new IllegalArgumentException("id must be provided");
        }
        if (document == null || document.isBlank()) {
            throw new IllegalArgumentException("document must be provided");
        }
        if (!DOCUMENTO_PATTERN.matcher(document).matches()) {
            throw new IllegalArgumentException("document must be a valid CPF (11 digits) or CNPJ (14 digits)");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must be provided");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("email must be a valid email address");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must be provided");
        }
    }

    public static Requester create (String document, String name, String email) {
        if (!isDocumentValid(document)){
            throw new DocumentInvalidException("Document must be a valid CPF (11 digits) or CNPJ (14 digits)");
        }
        return new Requester(UUID.randomUUID(), document, name, email, true, Instant.now());
    }

    public Requester deactivate() {
        return new Requester(id, document, name, email, false, createdAt);
    }

    private static boolean isDocumentValid(String document) {
        if (document == null || document.trim().isEmpty()) {
            return false;
        }

        CPFValidator cpfValidator = new CPFValidator();
        CNPJValidator cnpjValidator = new CNPJValidator();

        boolean isCpfValido = cpfValidator.invalidMessagesFor(document).isEmpty();
        boolean isCnpjValido = cnpjValidator.invalidMessagesFor(document).isEmpty();

        return isCpfValido || isCnpjValido;
    }
}
