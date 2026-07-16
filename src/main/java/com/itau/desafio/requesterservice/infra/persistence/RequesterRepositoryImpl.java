package com.itau.desafio.requesterservice.infra.persistence;

import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.domain.repository.RequesterRepository;
import org.springframework.stereotype.Repository;

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