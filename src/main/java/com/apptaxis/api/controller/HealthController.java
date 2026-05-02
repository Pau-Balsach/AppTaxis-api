package com.apptaxis.api.controller;

import com.apptaxis.api.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Estado de la API")
public class HealthController {

    @GetMapping
    @Operation(summary = "Comprueba si la API está activa", description = "Endpoint público, no requiere API key.")
    public ResponseEntity<ApiResponse<LocalDateTime>> health() {
        return ResponseEntity.ok(ApiResponse.ok("ok", LocalDateTime.now()));
    }
}