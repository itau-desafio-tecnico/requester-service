package com.itau.desafio.requesterservice.infra.persistence;
import com.itau.desafio.requesterservice.domain.model.Requester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        Requester requester = Requester.create("12345678901", "Maria Silva", "maria@teste.com");
        RequesterEntity entitySalva = new RequesterEntity(
                requester.id(), requester.document(), requester.name(),
                requester.email(), requester.active(), requester.createdAt());
        when(jpaRepository.save(org.mockito.ArgumentMatchers.any(RequesterEntity.class))).thenReturn(entitySalva);

        Requester saved = repository.save(requester);

        assertThat(saved).isEqualTo(requester);

        ArgumentCaptor<RequesterEntity> captor = ArgumentCaptor.forClass(RequesterEntity.class);
        org.mockito.Mockito.verify(jpaRepository).save(captor.capture());
        assertThat(captor.getValue().getDocument()).isEqualTo("12345678901");
    }

    @Test
    void shouldMapEntityToDomainWhenSearchingById() {
        Requester requester = Requester.create("12345678901", "Maria Silva", "maria@teste.com");
        RequesterEntity entity = new RequesterEntity(
                requester.id(), requester.document(), requester.name(),
                requester.email(), requester.active(), requester.createdAt());
        when(jpaRepository.findById(requester.id())).thenReturn(Optional.of(entity));

        Optional<Requester> encontrado = repository.findById(requester.id());

        assertThat(encontrado).contains(requester);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        java.util.UUID id = java.util.UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void shouldDelegateExistsByDocument() {
        when(jpaRepository.existsByDocument("12345678901")).thenReturn(true);

        assertThat(repository.existsByDocument("12345678901")).isTrue();
    }

    @Test
    void shouldMapEntityToDomainWhenSearchingByDocument() {
        Requester requester = Requester.create("12345678901", "Maria Silva", "maria@teste.com");
        RequesterEntity entity = new RequesterEntity(
                requester.id(), requester.document(), requester.name(),
                requester.email(), requester.active(), requester.createdAt());
        when(jpaRepository.findByDocument("12345678901")).thenReturn(Optional.of(entity));

        assertThat(repository.findByDocument("12345678901")).contains(requester);
    }
}
