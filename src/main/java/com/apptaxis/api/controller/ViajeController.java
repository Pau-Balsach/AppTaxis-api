package com.apptaxis.api.controller;

import com.apptaxis.api.model.Viaje;
import com.apptaxis.api.security.ApiKeyFilter;
import com.apptaxis.api.service.ViajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/viajes")
@Tag(name = "Viajes", description = "Gestión de viajes asignados a conductores")
@SecurityRequirement(name = "apiKey")
public class ViajeController {

    private final ViajeService service;

    public ViajeController(ViajeService service) {
        this.service = service;
    }

    private UUID adminId(HttpServletRequest req) {
        return (UUID) req.getAttribute(ApiKeyFilter.ADMIN_ID_ATTR);
    }

    @GetMapping
    @Operation(summary = "Listar todos los viajes del cliente autenticado")
    public List<Viaje> listarTodos(HttpServletRequest req) {
        return service.listarTodos(adminId(req));
    }

    @GetMapping("/conductor/{conductorId}")
    @Operation(summary = "Listar viajes de un conductor ordenados por fecha y hora")
    public List<Viaje> listarPorConductor(
            @PathVariable int conductorId,
            HttpServletRequest req) {
        return service.listarPorConductor(conductorId, adminId(req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar viaje por UUID")
    public ResponseEntity<Viaje> buscarPorId(
            @PathVariable UUID id,
            HttpServletRequest req) {
        return service.buscarPorId(id, adminId(req))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/conductor/{conductorId}")
    @Operation(summary = "Crear un viaje asignado a un conductor")
    public ResponseEntity<String> crear(
            @PathVariable int conductorId,
            @RequestBody Viaje viaje,
            HttpServletRequest req) {
        if (service.crear(viaje, conductorId, adminId(req)))
            return ResponseEntity.ok("Viaje creado correctamente.");
        return ResponseEntity.badRequest()
            .body("Error: conductor no encontrado con id " + conductorId
                + " o no pertenece a su cuenta.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un viaje existente")
    public ResponseEntity<String> editar(
            @PathVariable UUID id,
            @RequestBody Viaje datos,
            HttpServletRequest req) {
        if (service.editar(id, datos, adminId(req)))
            return ResponseEntity.ok("Viaje actualizado correctamente.");
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un viaje por UUID")
    public ResponseEntity<String> eliminar(
            @PathVariable UUID id,
            HttpServletRequest req) {
        if (service.eliminar(id, adminId(req)))
            return ResponseEntity.ok("Viaje eliminado correctamente.");
        return ResponseEntity.notFound().build();
    }
}