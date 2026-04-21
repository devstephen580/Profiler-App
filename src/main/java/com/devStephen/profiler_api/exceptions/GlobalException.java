package com.devStephen.profiler_api.exceptions;

import com.devStephen.profiler_api.dto.ApiResponse;
import com.devStephen.profiler_api.dto.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {

        return ResponseEntity.status(500).body(
                new ApiResponse<>("error",
                        "Upstream or server error",
                        null)
        );
    }

}
