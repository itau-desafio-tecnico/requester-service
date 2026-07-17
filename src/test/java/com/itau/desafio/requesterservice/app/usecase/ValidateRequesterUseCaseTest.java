package com.itau.desafio.requesterservice.app.usecase;

import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ValidateRequesterUseCaseTest {

    @Mock
    private RequesterRepository repository;

    private ValidateRequesterUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ValidateRequesterUseCase(repository);
    }

    @Test
    void deveRetornarTrueQuandoSolicitanteAtivo() {
        Requester ativo = Requester.create("12345678901", "Maria Silva", "maria@teste.com");
        when(repository.findById(ativo.id())).thenReturn(Optional.of(ativo));

        assertThat(useCase.execute(ativo.id())).isTrue();
    }

    @Test
    void deveRetornarFalseQuandoSolicitanteInativo() {
        Requester deactivate = Requester.create("12345678901", "Maria Silva", "maria@teste.com").deactivate();
        when(repository.findById(deactivate.id())).thenReturn(Optional.of(deactivate));

        assertThat(useCase.execute(deactivate.id())).isFalse();
    }

    @Test
    void deveRetornarFalseQuandoSolicitanteNaoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(useCase.execute(id)).isFalse();
    }
}
