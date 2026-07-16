package com.itau.desafio.requesterservice.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "requesters")
public class RequesterEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String document;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RequesterEntity() {
        // exigido pelo JPA
    }

    public RequesterEntity(UUID id, String document, String name, String email, boolean active, Instant createdAt) {
        this.id = id;
        this.document = document;
        this.name = name;
        this.email = email;
        this.active = active;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getDocument() {
        return document;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
