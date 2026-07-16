package com.itau.desafio.requesterservice.interfaces.rest;

import com.itau.desafio.requesterservice.app.usecase.CreateRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.GetRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.ValidateRequesterUseCase;
import com.itau.desafio.requesterservice.interfaces.rest.dto.RequesterRequest;
import com.itau.desafio.requesterservice.interfaces.rest.dto.RequesterResponse;
import com.itau.desafio.requesterservice.interfaces.rest.dto.ValidationResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/requester")
public class RequesterController {

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
        RequesterResponse requester = RequesterResponse.from(createRequesterUseCase.execute(request.document(), request.name(), request.email()));
        return ResponseEntity.created(URI.create("/requesters/"+requester.id())).body(requester);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequesterResponse> getById(@PathVariable UUID id) {
        RequesterResponse requester = RequesterResponse.from(getRequesterUseCase.byId(id));
        return ResponseEntity.ok(requester);
    }

    @GetMapping("/{id}/validation")
    public ResponseEntity<ValidationResponse> validate(@PathVariable UUID id) {
        boolean validation = validateRequesterUseCase.execute(id);
        return ResponseEntity.ok(new ValidationResponse(id, validation));
    }
}
