package com.devStephen.profiler_api.exceptions;

import com.devStephen.profiler_api.dto.ApiResponse;
import com.devStephen.profiler_api.dto.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalException {


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {

        return ResponseEntity.status(400).body(
                new ApiResponse<>("error", ex.getMessage(), null)
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {

        return ResponseEntity.status(404).body(
                new ApiResponse<>("error", ex.getMessage(), null)
        );
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleExternalApi(ExternalApiException ex) {

        return ResponseEntity.status(502).body(
                new ApiResponse<>(
                        "error",
                        ex.getMessage() + " returned an invalid response",
                        null
                )
        );
    }

    @ExceptionHandler(UnprocessableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnprocessable(UnprocessableException ex) {

        return ResponseEntity.status(422).body(
                new ApiResponse<>(
                        "error",
                        ex.getMessage(),
                        null
                )
        );
    }

  // ── Handles @Valid failures on request body ──────────────────────
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .findFirst()
        .orElse("Invalid request body");
    return ResponseEntity.status(400).body(
        new ApiResponse<>("error", message, null)
    );
  }

  // ── Handles wrong type for @RequestParam (e.g. letters for an int) ──
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.status(400).body(
        new ApiResponse<>("error", "Invalid query parameters, enter the correct query", null)
    );
  }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {

        return ResponseEntity.status(500).body(
                new ApiResponse<>("error",
                        "Upstream or server error",
                        null)
        );
    }

}
