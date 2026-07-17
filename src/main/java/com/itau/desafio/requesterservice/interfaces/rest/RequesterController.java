package com.itau.desafio.requesterservice.interfaces.rest;

import com.itau.desafio.requesterservice.app.usecase.CreateRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.GetRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.ValidateRequesterUseCase;
import com.itau.desafio.requesterservice.interfaces.rest.dto.RequesterRequest;
import com.itau.desafio.requesterservice.interfaces.rest.dto.RequesterResponse;
import com.itau.desafio.requesterservice.interfaces.rest.dto.ValidationResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/requesters")
public class RequesterController {

    private static final Logger log = LoggerFactory.getLogger(RequesterController.class);

    private final CreateRequesterUseCase  createRequesterUseCase;
    private final GetRequesterUseCase getRequesterUseCase;
    private final ValidateRequesterUseCase validateRequesterUseCase;

    public RequesterController(CreateRequesterUseCase createRequesterUseCase, GetRequesterUseCase getRequesterUseCase, ValidateRequesterUseCase validateRequesterUseCase) {
        this.createRequesterUseCase = createRequesterUseCase;
        this.getRequesterUseCase = getRequesterUseCase;
        this.validateRequesterUseCase = validateRequesterUseCase;
    }

    @PostMapping
    public ResponseEntity<RequesterResponse> create(@Valid @RequestBody RequesterRequest request) {
        log.info("Creating requester document={}", mask(request.document()));
        RequesterResponse requester = RequesterResponse.from(createRequesterUseCase.execute(request.document(), request.name(), request.email()));
        log.info("Requester created id={}", requester.id());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(requester.id())
                .toUri();
        return ResponseEntity.created(location).body(requester);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequesterResponse> getById(@PathVariable UUID id) {
        log.info("Fetching requester id={}", id);
        RequesterResponse requester = RequesterResponse.from(getRequesterUseCase.byId(id));
        return ResponseEntity.ok(requester);
    }

    @GetMapping("/{id}/validation")
    public ResponseEntity<ValidationResponse> validate(@PathVariable UUID id) {
        log.info("Validating requester id={}", id);
        boolean validation = validateRequesterUseCase.execute(id);
        log.info("Requester id={} active={}", id, validation);
        return ResponseEntity.ok(new ValidationResponse(id, validation));
    }

    private static String mask(String document) {
        if (document == null || document.length() < 4) {
            return "***";
        }
        return "*".repeat(document.length() - 2) + document.substring(document.length() - 2);
    }
}
