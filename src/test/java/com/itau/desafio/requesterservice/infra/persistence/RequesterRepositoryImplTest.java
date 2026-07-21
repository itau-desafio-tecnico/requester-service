package com.itau.desafio.requesterservice.infra.persistence;
import com.itau.desafio.requesterservice.domain.model.PagedResult;
import com.itau.desafio.requesterservice.domain.model.Requester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequesterRepositoryImplTest {
    @Mock
    private RequesterJpaRepository jpaRepository;

    private RequesterRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new RequesterRepositoryImpl(jpaRepository);
    }

    @Test
    void shouldMapDomainToEntityAndSave() {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        RequesterEntity savedEntity = new RequesterEntity(
                requester.id(), requester.document(), requester.name(),
                requester.email(), requester.active(), requester.createdAt());
        when(jpaRepository.save(org.mockito.ArgumentMatchers.any(RequesterEntity.class))).thenReturn(savedEntity);

        Requester saved = repository.save(requester);

        assertThat(saved).isEqualTo(requester);

        ArgumentCaptor<RequesterEntity> captor = ArgumentCaptor.forClass(RequesterEntity.class);
        org.mockito.Mockito.verify(jpaRepository).save(captor.capture());
        assertThat(captor.getValue().getDocument()).isEqualTo("11144477735");
    }

    @Test
    void shouldMapEntityToDomainWhenSearchingById() {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        RequesterEntity entity = new RequesterEntity(
                requester.id(), requester.document(), requester.name(),
                requester.email(), requester.active(), requester.createdAt());
        when(jpaRepository.findById(requester.id())).thenReturn(Optional.of(entity));

        Optional<Requester> found = repository.findById(requester.id());

        assertThat(found).contains(requester);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        java.util.UUID id = java.util.UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void shouldDelegateExistsByDocument() {
        when(jpaRepository.existsByDocument("11144477735")).thenReturn(true);

        assertThat(repository.existsByDocument("11144477735")).isTrue();
    }

    @Test
    void shouldMapEntityToDomainWhenSearchingByDocument() {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        RequesterEntity entity = new RequesterEntity(
                requester.id(), requester.document(), requester.name(),
                requester.email(), requester.active(), requester.createdAt());
        when(jpaRepository.findByDocument("11144477735")).thenReturn(Optional.of(entity));

        assertThat(repository.findByDocument("11144477735")).contains(requester);
    }

    @Test
    void shouldListAllRequestersWhenActiveFilterIsNull() {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        RequesterEntity entity = new RequesterEntity(
                requester.id(), requester.document(), requester.name(),
                requester.email(), requester.active(), requester.createdAt());
        Page<RequesterEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);
        when(jpaRepository.findAll(any(Pageable.class))).thenReturn(page);

        PagedResult<Requester> result = repository.findAll(0, 20, null);

        assertThat(result.content()).containsExactly(requester);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(20);
    }

    @Test
    void shouldListRequestersFilteredByActive() {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        RequesterEntity entity = new RequesterEntity(
                requester.id(), requester.document(), requester.name(),
                requester.email(), requester.active(), requester.createdAt());
        Page<RequesterEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 20), 1);
        when(jpaRepository.findByActive(eq(true), any(Pageable.class))).thenReturn(page);

        PagedResult<Requester> result = repository.findAll(0, 20, true);

        assertThat(result.content()).containsExactly(requester);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void shouldReturnEmptyPagedResultWhenNoRequestersMatch() {
        Page<RequesterEntity> page = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(jpaRepository.findByActive(eq(false), any(Pageable.class))).thenReturn(page);

        PagedResult<Requester> result = repository.findAll(0, 20, false);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
        assertThat(result.totalPages()).isZero();
    }
}
