package com.itau.desafio.requesterservice.app.usecase;

import com.itau.desafio.requesterservice.domain.exception.RequesterNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class GetRequesterUseCaseTest {
    @Mock
    private RequesterRepository repository;

    private GetRequesterUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetRequesterUseCase(repository);
    }

    @Test
    void deveRetornarSolicitanteQuandoExiste() {
        Requester solicitante = Requester.create("12345678901", "Maria Silva", "maria@teste.com");
        when(repository.findById(solicitante.id())).thenReturn(Optional.of(solicitante));

        Requester encontrado = useCase.byId(solicitante.id());

        assertThat(encontrado).isEqualTo(solicitante);
    }

    @Test
    void deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.byId(id))
                .isInstanceOf(RequesterNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
