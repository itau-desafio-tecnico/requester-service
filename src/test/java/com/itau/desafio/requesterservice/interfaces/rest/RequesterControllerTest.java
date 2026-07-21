package com.itau.desafio.requesterservice.interfaces.rest;

import com.itau.desafio.requesterservice.app.usecase.CreateRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.GetRequesterUseCase;
import com.itau.desafio.requesterservice.app.usecase.ListRequestersUseCase;
import com.itau.desafio.requesterservice.app.usecase.ValidateRequesterUseCase;
import com.itau.desafio.requesterservice.domain.exception.DocumentInvalidException;
import com.itau.desafio.requesterservice.domain.exception.RequesterAlreadyExistsException;
import com.itau.desafio.requesterservice.domain.exception.RequesterNotFoundException;
import com.itau.desafio.requesterservice.domain.model.PagedResult;
import com.itau.desafio.requesterservice.domain.model.Requester;
import com.itau.desafio.requesterservice.interfaces.rest.dto.RequesterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
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

    @MockitoBean
    private ListRequestersUseCase listRequestersUseCase;

    @Test
    void shouldCreateRequesterAndReturn201() throws Exception {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        when(createRequesterUseCase.execute("11144477735", "Maria Silva", "maria@teste.com"))
                .thenReturn(requester);

        RequesterRequest request = new RequesterRequest("11144477735", "Maria Silva", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.document").value("11144477735"))
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
    void shouldReturn400WhenDocumentHasInvalidCheckDigit() throws Exception {
        when(createRequesterUseCase.execute(any(), any(), any()))
                .thenThrow(new DocumentInvalidException("Document must be a valid CPF (11 digits) or CNPJ (14 digits)"));

        RequesterRequest request = new RequesterRequest("12345678901", "Maria Silva", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DOCUMENT_INVALID"));
    }

    @Test
    void shouldReturn400WhenDocumentIsShorterThanMinLength() throws Exception {
        RequesterRequest request = new RequesterRequest("1234567890", "Maria Silva", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"));
    }

    @Test
    void shouldReturn400WhenDocumentIsLongerThanMaxLength() throws Exception {
        RequesterRequest request = new RequesterRequest("123456789012345", "Maria Silva", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"));
    }

    @Test
    void shouldReturn400WhenNameIsShorterThanMinLength() throws Exception {
        RequesterRequest request = new RequesterRequest("11144477735", "A", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"));
    }

    @Test
    void shouldReturn400WhenNameIsLongerThanMaxLength() throws Exception {
        RequesterRequest request = new RequesterRequest("11144477735", "A".repeat(101), "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION"));
    }

    @Test
    void shouldCreateRequesterWhenNameIsAtMinLengthBoundary() throws Exception {
        Requester requester = Requester.create("11144477735", "Ab", "maria@teste.com");
        when(createRequesterUseCase.execute("11144477735", "Ab", "maria@teste.com")).thenReturn(requester);

        RequesterRequest request = new RequesterRequest("11144477735", "Ab", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldCreateRequesterWhenNameIsAtMaxLengthBoundary() throws Exception {
        String name = "A".repeat(100);
        Requester requester = Requester.create("11144477735", name, "maria@teste.com");
        when(createRequesterUseCase.execute("11144477735", name, "maria@teste.com")).thenReturn(requester);

        RequesterRequest request = new RequesterRequest("11144477735", name, "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn409WhenDocumentIsDuplicated() throws Exception {
        when(createRequesterUseCase.execute(any(), any(), any()))
                .thenThrow(new RequesterAlreadyExistsException("11144477735"));

        RequesterRequest request = new RequesterRequest("11144477735", "Maria Silva", "maria@teste.com");

        mockMvc.perform(post("/requesters")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("REQUESTER_ALREADY_EXISTS"));
    }

    @Test
    void shouldGetRequesterById() throws Exception {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        when(getRequesterUseCase.byId(requester.id())).thenReturn(requester);

        mockMvc.perform(get("/requesters/{id}", requester.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value("11144477735"));
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
    void shouldReturn400WhenIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(get("/requesters/{id}", "not-a-valid-uuid"))
                .andExpect(status().isBadRequest());
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

    @Test
    void shouldReturn400WhenValidationIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(get("/requesters/{id}/validation", "not-a-valid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListRequestersWithDefaultPagination() throws Exception {
        Requester requester = Requester.create("11144477735", "Maria Silva", "maria@teste.com");
        PagedResult<Requester> pagedResult = PagedResult.of(List.of(requester), 0, 20, 1);
        when(listRequestersUseCase.execute(0, 20, null)).thenReturn(pagedResult);

        mockMvc.perform(get("/requesters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].document").value("11144477735"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldListRequestersFilteredByActive() throws Exception {
        PagedResult<Requester> pagedResult = PagedResult.of(List.of(), 0, 20, 0);
        when(listRequestersUseCase.execute(0, 20, true)).thenReturn(pagedResult);

        mockMvc.perform(get("/requesters").param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldListRequestersWithCustomPagination() throws Exception {
        PagedResult<Requester> pagedResult = PagedResult.of(List.of(), 2, 5, 0);
        when(listRequestersUseCase.execute(2, 5, null)).thenReturn(pagedResult);

        mockMvc.perform(get("/requesters").param("page", "2").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    void shouldReturn400WhenPageIsNotANumber() throws Exception {
        mockMvc.perform(get("/requesters").param("page", "abc"))
                .andExpect(status().isBadRequest());
    }
}
