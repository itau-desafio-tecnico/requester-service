package com.itau.desafio.requesterservice.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequesterJpaRepository extends JpaRepository<RequesterEntity, Long> {
}
