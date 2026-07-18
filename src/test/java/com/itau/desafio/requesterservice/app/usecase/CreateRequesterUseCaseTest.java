package com.itau.desafio.requesterservice.app.usecase;

import com.itau.desafio.requesterservice.domain.exception.RequesterAlreadyExistsException;
import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRequesterUseCaseTest {

    @Mock
    private RequesterRepository repository;

    private CreateRequesterUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateRequesterUseCase(repository);
    }

    @Test
    void shouldCreateRequesterWhenDocumentDoesNotExist() {
        when(repository.existsByDocument("11144477735")).thenReturn(false);
        when(repository.save(any(Requester.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Requester created = useCase.execute("11144477735", "Maria Silva", "maria@teste.com");

        assertThat(created.document()).isEqualTo("11144477735");
        assertThat(created.active()).isTrue();

        ArgumentCaptor<Requester> captor = ArgumentCaptor.forClass(Requester.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().name()).isEqualTo("Maria Silva");
    }

    @Test
    void shouldRejectWhenDocumentAlreadyExists() {
        when(repository.existsByDocument("11144477735")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute("11144477735", "Maria Silva", "maria@teste.com"))
                .isInstanceOf(RequesterAlreadyExistsException.class)
                .hasMessageContaining("11144477735");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldRejectWhenDocumentHasInvalidCheckDigit() {
        assertThatThrownBy(() -> useCase.execute("12345678901", "Maria Silva", "maria@teste.com"))
                .isInstanceOf(com.itau.desafio.requesterservice.domain.exception.DocumentInvalidException.class);

        verify(repository, never()).save(any());
    }
}
