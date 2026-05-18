package com.example.server.exception;

import com.example.server.dto.common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleResourceNotFound() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("User", "id", 1L);

        // Act
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleResourceNotFound(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("User not found with id : '1'"));
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
    }

    @Test
    void shouldHandleAppException() {
        // Arrange
        AppException ex = new AppException(HttpStatus.BAD_REQUEST, "Business error", "BUSINESS_ERROR");

        // Act
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleAppException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Business error", response.getBody().getMessage());
        assertEquals("BUSINESS_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void shouldHandleAccessDenied() {
        // Arrange
        org.springframework.security.access.AccessDeniedException ex = new org.springframework.security.access.AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleAccessDenied(ex);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Access denied"));
        assertEquals("FORBIDDEN", response.getBody().getErrorCode());
    }

    @Test
    void shouldHandleOptimisticLockingFailure() {
        // Arrange
        org.springframework.orm.ObjectOptimisticLockingFailureException ex = new org.springframework.orm.ObjectOptimisticLockingFailureException("concurrency", new RuntimeException());

        // Act
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleOptimisticLockingFailure(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("modified by another user"));
        assertEquals("CONCURRENCY_FAILURE", response.getBody().getErrorCode());
    }

    @Test
    void shouldHandleAppExceptionWithNullStatus() {
        // Arrange
        AppException ex = new AppException(null, "Business error", "BUSINESS_ERROR");

        // Act
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleAppException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Business error", response.getBody().getMessage());
        assertEquals("BUSINESS_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void shouldHandleDataIntegrityViolation() {
        // Arrange
        org.springframework.dao.DataIntegrityViolationException ex = new org.springframework.dao.DataIntegrityViolationException("constraint violation");

        // Act
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleDataIntegrityViolation(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Database constraint violation"));
        assertEquals("DATABASE_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void shouldHandleGlobalException() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleGlobalException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("Unexpected error"));
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
    }

    @Test
    void shouldHandleValidationExceptions() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertNotNull(response.getBody().getData());
    }
}
