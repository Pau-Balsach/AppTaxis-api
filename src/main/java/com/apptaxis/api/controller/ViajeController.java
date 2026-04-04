package com.apptaxis.api.controller;

import com.apptaxis.api.model.Viaje;
import com.apptaxis.api.service.ViajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/viajes")
@Tag(name = "Viajes", description = "Gestion de viajes asignados a conductores")
public class ViajeController {

    private final ViajeService service;

    public ViajeController(ViajeService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los viajes")
    public List<Viaje> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/conductor/{conductorId}")
    @Operation(summary = "Listar viajes de un conductor ordenados por fecha y hora")
    public List<Viaje> listarPorConductor(@PathVariable int conductorId) {
        return service.listarPorConductor(conductorId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar viaje por UUID")
    public ResponseEntity<Viaje> buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/conductor/{conductorId}")
    @Operation(summary = "Crear un viaje asignado a un conductor")
    public ResponseEntity<String> crear(@PathVariable int conductorId,
                                         @RequestBody Viaje viaje) {
        if (service.crear(viaje, conductorId))
            return ResponseEntity.ok("Viaje creado correctamente.");
        return ResponseEntity.badRequest()
            .body("Error: conductor no encontrado con id " + conductorId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un viaje existente")
    public ResponseEntity<String> editar(@PathVariable UUID id,
                                          @RequestBody Viaje datos) {
        if (service.editar(id, datos))
            return ResponseEntity.ok("Viaje actualizado correctamente.");
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un viaje por UUID")
    public ResponseEntity<String> eliminar(@PathVariable UUID id) {
        if (service.eliminar(id))
            return ResponseEntity.ok("Viaje eliminado correctamente.");
        return ResponseEntity.notFound().build();
    }
}
