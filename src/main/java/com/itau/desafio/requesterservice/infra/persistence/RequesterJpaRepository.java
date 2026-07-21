package com.itau.desafio.requesterservice.infra.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RequesterJpaRepository extends JpaRepository<RequesterEntity, UUID> {
    Optional<RequesterEntity> findByDocument(String document);

    boolean existsByDocument(String document);

    Page<RequesterEntity> findByActive(boolean active, Pageable pageable);
}
