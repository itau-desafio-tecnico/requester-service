package com.itau.desafio.requesterservice.infra.persistence;

import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RequesterRepositoryImpl implements RequesterRepository {
    private final RequesterJpaRepository repository;

    public RequesterRepositoryImpl(RequesterJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Requester save(Requester requester) {
        RequesterEntity entity = toEntity(requester);
        repository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Requester> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Requester> findByDocument(String document) {
        return repository.findByDocument(document).map(this::toDomain);
    }

    @Override
    public boolean existsByDocument(String document) {
        return repository.existsByDocument(document);
    }

    private RequesterEntity toEntity(Requester requester) {
        return new RequesterEntity(
                requester.id(),
                requester.document(),
                requester.name(),
                requester.email(),
                requester.active(),
                requester.createdAt()
        );
    }

    private Requester toDomain(RequesterEntity entity) {
        return new Requester(
                entity.getId(),
                entity.getDocument(),
                entity.getName(),
                entity.getEmail(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}