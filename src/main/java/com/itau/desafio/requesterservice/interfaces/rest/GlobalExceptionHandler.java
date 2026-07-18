package com.itau.desafio.requesterservice.interfaces.rest;

import com.itau.desafio.requesterservice.domain.exception.DocumentInvalidException;
import com.itau.desafio.requesterservice.domain.exception.RequesterAlreadyExistsException;
import com.itau.desafio.requesterservice.domain.exception.RequesterNotFoundException;
import com.itau.desafio.requesterservice.interfaces.rest.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RequesterNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(RequesterNotFoundException ex) {
        log.warn("Requester not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("REQUESTER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(RequesterAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(RequesterAlreadyExistsException ex) {
        log.warn("Requester already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("REQUESTER_ALREADY_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(DocumentInvalidException.class)
    public ResponseEntity<ErrorResponse> handleDocumentInvalid(DocumentInvalidException ex) {
        log.warn("Invalid document: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("DOCUMENT_INVALID", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid request: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = ex.getName() + ": must be a valid " + ex.getRequiredType().getSimpleName();
        log.warn("Invalid path parameter: {}", message);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_REQUEST", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", message);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unexpected error handling request", ex);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("INTERNAL_ERROR", "Internal Server Error"));
    }
}
