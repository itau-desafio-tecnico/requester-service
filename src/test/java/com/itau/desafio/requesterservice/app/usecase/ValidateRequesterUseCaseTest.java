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
    void shouldReturnTrueWhenRequesterIsActive() {
        Requester active = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        when(repository.findById(active.id())).thenReturn(Optional.of(active));

        assertThat(useCase.execute(active.id())).isTrue();
    }

    @Test
    void shouldReturnFalseWhenRequesterIsInactive() {
        Requester deactivate = Requester.create("11144477735", "Maria Silva", "maria@teste.com").deactivate();
        when(repository.findById(deactivate.id())).thenReturn(Optional.of(deactivate));

        assertThat(useCase.execute(deactivate.id())).isFalse();
    }

    @Test
    void shouldReturnFalseWhenRequesterDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(useCase.execute(id)).isFalse();
    }
}
