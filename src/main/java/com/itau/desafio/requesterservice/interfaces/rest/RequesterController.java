package com.itau.desafio.requesterservice.interfaces.rest;

import com.itau.desafio.requesterservice.app.usecase.CreateRequesterUseCase;
import com.itau.desafio.requesterservice.interfaces.rest.dto.RequesterRequest;
import com.itau.desafio.requesterservice.interfaces.rest.dto.RequesterResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/requesters")
public class RequesterController {

    private final CreateRequesterUseCase  createRequesterUseCase;

    public RequesterController(CreateRequesterUseCase createRequesterUseCase) {
        this.createRequesterUseCase = createRequesterUseCase;
    }

    @PostMapping
    public ResponseEntity<RequesterResponse> create(@Valid @RequestBody RequesterRequest request) {
        RequesterResponse requester = RequesterResponse.from();
        return ResponseEntity.created(URI.create("/requesters/"+requester.id())).body(requester);
    }
}
