package com.itau.desafio.requesterservice.interfaces.rest;

import com.itau.desafio.requesterservice.app.usecase.CreateRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.GetRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.ValidateRequesterUseCase;
import com.itau.desafio.requesterservice.domain.exception.RequesterAlreadyExistsException;
import com.itau.desafio.requesterservice.domain.exception.RequesterNotFoundException;
import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.interfaces.rest.dto.RequesterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequesterController.class)
public class RequesterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateRequesterUseCase createRequesterUseCase;

    @MockitoBean
    private GetRequesterUseCase getRequesterUseCase;

    @MockitoBean
    private ValidateRequesterUseCase validateRequesterUseCase;

    @Test
    void shouldCreateRequesterAndReturn201() throws Exception {
        Requester requester = Requester.create("12345678901", "Maria Silva", "maria@teste.com");
        when(createRequesterUseCase.execute("12345678901", "Maria Silva", "maria@teste.com"))
                .thenReturn(requester);

        RequesterRequest request = new RequesterRequest("12345678901", "Maria Silva", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.document").value("12345678901"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        RequesterRequest request = new RequesterRequest("", "", "invalido");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenDocumentIsDuplicated() throws Exception {
        when(createRequesterUseCase.execute(any(), any(), any()))
                .thenThrow(new RequesterAlreadyExistsException("12345678901"));

        RequesterRequest request = new RequesterRequest("12345678901", "Maria Silva", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("REQUESTER_ALREADY_EXISTS"));
    }

    @Test
    void shouldGetRequesterById() throws Exception {
        Requester requester = Requester.create("12345678901", "Maria Silva", "maria@teste.com");
        when(getRequesterUseCase.byId(requester.id())).thenReturn(requester);

        mockMvc.perform(get("/requesters/{id}", requester.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value("12345678901"));
    }

    @Test
    void shouldReturn404WhenRequesterDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(getRequesterUseCase.byId(id)).thenThrow(new RequesterNotFoundException(id.toString()));

        mockMvc.perform(get("/requesters/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("REQUESTER_NOT_FOUND"));
    }

    @Test
    void shouldValidateRequester() throws Exception {
        UUID id = UUID.randomUUID();
        when(validateRequesterUseCase.execute(id)).thenReturn(true);

        mockMvc.perform(get("/requesters/{id}/validation", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validation").value(true))
                .andExpect(jsonPath("$.requesterId").value(id.toString()));
    }

    @Test
    void shouldValidateNonExistentRequester() throws Exception {
        UUID id = UUID.randomUUID();
        when(validateRequesterUseCase.execute(id)).thenReturn(false);

        mockMvc.perform(get("/requesters/{id}/validation", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validation").value(false));
    }
}
