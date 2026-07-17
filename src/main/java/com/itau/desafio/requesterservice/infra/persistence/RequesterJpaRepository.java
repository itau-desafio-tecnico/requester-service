package com.itau.desafio.requesterservice.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RequesterJpaRepository extends JpaRepository<RequesterEntity, UUID> {
    Optional<RequesterEntity> findByDocument(String document);

    boolean existsByDocument(String document);
}
