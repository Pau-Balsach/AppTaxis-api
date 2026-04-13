package com.apptaxis.api.controller;

import com.apptaxis.api.model.Conductor;
import com.apptaxis.api.security.ApiKeyFilter;
import com.apptaxis.api.service.ConductorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conductores")
@Tag(name = "Conductores", description = "Gestión de conductores de la flota")
@SecurityRequirement(name = "apiKey")
public class ConductorController {

    private final ConductorService service;

    public ConductorController(ConductorService service) {
        this.service = service;
    }

    /** Extrae el adminId inyectado por ApiKeyFilter. */
    private UUID adminId(HttpServletRequest req) {
        return (UUID) req.getAttribute(ApiKeyFilter.ADMIN_ID_ATTR);
    }

    @GetMapping
    @Operation(summary = "Listar todos los conductores del cliente autenticado")
    public List<Conductor> listarTodos(HttpServletRequest req) {
        return service.listarTodos(adminId(req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conductor por ID")
    public ResponseEntity<Conductor> buscarPorId(
            @PathVariable int id,
            HttpServletRequest req) {
        return service.buscarPorId(id, adminId(req))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo conductor",
               description = "La matrícula debe tener formato 1234ABC. "
                           + "El conductor queda vinculado al cliente autenticado.")
    public ResponseEntity<String> registrar(
            @RequestBody Conductor conductor,
            HttpServletRequest req) {
        if (service.registrar(conductor, adminId(req)))
            return ResponseEntity.ok("Conductor registrado correctamente.");
        return ResponseEntity.badRequest()
            .body("Error: matrícula inválida, en blanco, o ya existe.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar el nombre de un conductor")
    public ResponseEntity<String> editar(
            @PathVariable int id,
            @RequestParam String nuevoNombre,
            HttpServletRequest req) {
        if (service.editar(id, nuevoNombre, adminId(req)))
            return ResponseEntity.ok("Conductor actualizado correctamente.");
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un conductor por ID")
    public ResponseEntity<String> eliminar(
            @PathVariable int id,
            HttpServletRequest req) {
        if (service.eliminar(id, adminId(req)))
            return ResponseEntity.ok("Conductor eliminado correctamente.");
        return ResponseEntity.notFound().build();
    }
}