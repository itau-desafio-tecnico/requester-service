package com.itau.desafio.requesterservice.domain.model;

import com.itau.desafio.requesterservice.domain.exception.DocumentInvalidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequesterTest {
    @Test
    void shouldCreateRequester() {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");

        assertThat(requester.id()).isNotNull();
        assertThat(requester.document()).isEqualTo("11144477735");
        assertThat(requester.name()).isEqualTo("Maria Silva");
        assertThat(requester.email()).isEqualTo("maria@teste.com");
        assertThat(requester.active()).isTrue();
        assertThat(requester.createdAt()).isNotNull();
    }

    @Test
    void shouldInactivateRequester() {
        Requester active = Requester.create("11144477735", "Maria Silva", "maria@teste.com");

        Requester deactivated = active.deactivate();

        assertThat(deactivated.active()).isFalse();
        assertThat(deactivated.id()).isEqualTo(active.id());
    }

    @Test
    void shouldRejectIdNull() {
        assertThatThrownBy(() -> new Requester(null, "11144477735", "Maria", "maria@teste.com", true, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "123", "abc12345678", "123456789012345"})
    void shouldRejectInvalidDocument(String invalidDocument) {
        assertThatThrownBy(() -> Requester.create(invalidDocument, "Maria", "maria@teste.com"))
                .isInstanceOf(DocumentInvalidException.class);
    }

    @Test
    void shouldRejectCpfWithInvalidCheckDigit() {
        assertThatThrownBy(() -> Requester.create("12345678901", "Maria", "maria@teste.com"))
                .isInstanceOf(DocumentInvalidException.class);
    }

    @Test
    void shouldRejectCpfWithAllDigitsEqual() {
        assertThatThrownBy(() -> Requester.create("11111111111", "Maria", "maria@teste.com"))
                .isInstanceOf(DocumentInvalidException.class);
    }

    @Test
    void shouldRejectCnpjWithInvalidCheckDigit() {
        assertThatThrownBy(() -> Requester.create("12345678000199", "Empresa LTDA", "contato@empresa.com"))
                .isInstanceOf(DocumentInvalidException.class);
    }

    @Test
    void shouldRejectNameEmpty() {
        assertThatThrownBy(() -> Requester.create("11144477735", "  ", "maria@teste.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"sememail", "@teste.com", "maria@", "maria@teste"})
    void shouldRejectInvalidEmail(String invalidEmail) {
        assertThatThrownBy(() -> Requester.create("11144477735", "Maria", invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
    }

    @Test
    void shouldAcceptValidCnpj() {
        Requester requester = Requester.create("34131647525560", "Empresa LTDA", "contato@empresa.com");

        assertThat(requester.document()).isEqualTo("34131647525560");
    }
}
