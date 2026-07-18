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
    void shouldReturnRequesterWhenExists() {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        when(repository.findById(requester.id())).thenReturn(Optional.of(requester));

        Requester found = useCase.byId(requester.id());

        assertThat(found).isEqualTo(requester);
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.byId(id))
                .isInstanceOf(RequesterNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
