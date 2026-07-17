package com.itau.desafio.requesterservice.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequesterTest {
    @Test
    void shouldCreateRequester() {
        Requester requester = Requester.create("12345678901", "Maria Silva", "maria@teste.com");

        assertThat(requester.id()).isNotNull();
        assertThat(requester.document()).isEqualTo("12345678901");
        assertThat(requester.name()).isEqualTo("Maria Silva");
        assertThat(requester.email()).isEqualTo("maria@teste.com");
        assertThat(requester.active()).isTrue();
        assertThat(requester.createdAt()).isNotNull();
    }

    @Test
    void shouldInactivateRequester() {
        Requester active = Requester.create("12345678901", "Maria Silva", "maria@teste.com");

        Requester deactivated = active.deactivate();

        assertThat(deactivated.active()).isFalse();
        assertThat(deactivated.id()).isEqualTo(active.id());
    }

    @Test
    void shouldRejectIdNull() {
        assertThatThrownBy(() -> new Requester(null, "12345678901", "Maria", "maria@teste.com", true, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "123", "abc12345678", "123456789012345"})
    void shouldRejectInvalidDocument(String invalidDocument) {
        assertThatThrownBy(() -> Requester.create(invalidDocument, "Maria", "maria@teste.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNameEmpty() {
        assertThatThrownBy(() -> Requester.create("12345678901", "  ", "maria@teste.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"sememail", "@teste.com", "maria@", "maria@teste"})
    void shouldRejectInvalidEmail(String invalidEmail) {
        assertThatThrownBy(() -> Requester.create("12345678901", "Maria", invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void shouldAcceptValidCnpj() {
        Requester requester = Requester.create("12345678000199", "Empresa LTDA", "contato@empresa.com");

        assertThat(requester.document()).isEqualTo("12345678000199");
    }
}
