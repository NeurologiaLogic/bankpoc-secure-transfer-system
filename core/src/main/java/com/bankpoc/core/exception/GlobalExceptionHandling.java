package com.bankpoc.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandling {

    /**
     * ✅ Handles all custom API exceptions thrown using ApiException.*
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        log.warn("[API ERROR] {} - {}", ex.getStatus(), ex.getMessage());
        return buildResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * ✅ Handles validation errors from @Valid annotated requests.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        Map<String, String> details = new HashMap<>();
        fieldErrors.forEach(err -> details.put(err.getField(), err.getDefaultMessage()));

        log.debug("[VALIDATION FAILED] {}", details);

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed.", details);
    }

    /**
     * ✅ Catch-all handler for unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("[UNEXPECTED ERROR]", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    /**
     * ✅ Catch-all handler for Data Integrity Violation
     * Will get added lots more
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("[DATA INTEGRITY VIOLATIONS]", ex);
        String msg = ex.getMessage().toLowerCase();
        //----------- USERS ------------
        if (msg.contains("users_phone_number_key")) {
            return buildResponse(HttpStatus.CONFLICT,"Phone number is already registered.");
        } else if (msg.contains("users_email_key")) {
            return buildResponse(HttpStatus.CONFLICT,"Email is already registered.");
        }

        return buildResponse(HttpStatus.CONFLICT,"Duplicate data violation.");
    }

    // =====================================================
    // 🔧 Helper: Standard JSON structure for every error
    // =====================================================
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return buildResponse(status, message, null);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message, Map<String, String> details) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (details != null && !details.isEmpty()) {
            body.put("details", details);
        }

        return ResponseEntity.status(status).body(body);
    }
}
