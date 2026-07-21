package com.itau.desafio.requesterservice.app.usecase;

import com.itau.desafio.requesterservice.domain.model.PagedResult;
import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRequestersUseCaseTest {
    @Mock
    private RequesterRepository repository;

    private ListRequestersUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListRequestersUseCase(repository);
    }

    @Test
    void shouldReturnPagedResultFromRepository() {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        PagedResult<Requester> pagedResult = PagedResult.of(List.of(requester), 0, 20, 1);
        when(repository.findAll(0, 20, null)).thenReturn(pagedResult);

        PagedResult<Requester> result = useCase.execute(0, 20, null);

        assertThat(result).isEqualTo(pagedResult);
    }

    @Test
    void shouldForwardActiveFilterToRepository() {
        PagedResult<Requester> pagedResult = PagedResult.of(List.of(), 0, 20, 0);
        when(repository.findAll(0, 20, true)).thenReturn(pagedResult);

        PagedResult<Requester> result = useCase.execute(0, 20, true);

        assertThat(result.content()).isEmpty();
    }
}
