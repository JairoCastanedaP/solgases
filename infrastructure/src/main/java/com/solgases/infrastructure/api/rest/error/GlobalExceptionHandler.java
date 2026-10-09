package com.solgases.infrastructure.api.rest.error;

import com.solgases.application.exception.CategoryNotFoundException;
import com.solgases.application.exception.DuplicateCategoryNameException;
import com.solgases.application.exception.DuplicateUnitOfMeasureCodeException;
import com.solgases.application.exception.UnitOfMeasureNotFoundException;
import com.solgases.application.exception.DuplicateProductSkuException;
import com.solgases.application.exception.ProductNotFoundException;
import com.solgases.application.exception.InventoryMovementNotFoundException;
import com.solgases.application.exception.AuthenticationFailedException;
import com.solgases.application.exception.ConflictException;
import com.solgases.application.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Translates exceptions into RFC 9457 {@link ProblemDetail} responses.
 * Standard Spring MVC errors (malformed JSON, type mismatch, unsupported method or media type)
 * are already handled by the base class. Any other exception becomes a generic 500 response.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFound(CategoryNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateCategoryNameException.class)
    public ProblemDetail handleDuplicateCategoryName(DuplicateCategoryNameException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UnitOfMeasureNotFoundException.class)
    public ProblemDetail handleUnitOfMeasureNotFound(UnitOfMeasureNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateUnitOfMeasureCodeException.class)
    public ProblemDetail handleDuplicateUnitOfMeasureCode(DuplicateUnitOfMeasureCodeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ProblemDetail handleProductNotFound(ProductNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InventoryMovementNotFoundException.class)
    public ProblemDetail handleInventoryMovementNotFound(InventoryMovementNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateProductSkuException.class)
    public ProblemDetail handleDuplicateProductSku(DuplicateProductSkuException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** Failed login. The detail is generic and never reveals which part of the credentials was wrong. */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ProblemDetail handleAuthenticationFailed(AuthenticationFailedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /** Security exceptions raised inside a controller must not end up as 500 responses. */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleSecurityAuthentication(AuthenticationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                "Authentication is required to access this resource");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource");
    }

    /**
     * Fallback for unexpected errors. The response does not reveal internal details; the exception is logged
     * with its stack trace, but no request body is logged.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error while processing the request", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", fieldError.getDefaultMessage() == null ? "invalid value" : fieldError.getDefaultMessage()))
                .toList();

        ProblemDetail problem = ex.getBody();
        problem.setDetail("Validation failed");
        problem.setProperty("errors", errors);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }
}
