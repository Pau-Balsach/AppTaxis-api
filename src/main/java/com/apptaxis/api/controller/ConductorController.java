package com.apptaxis.api.controller;

import com.apptaxis.api.model.Conductor;
import com.apptaxis.api.service.ConductorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conductores")
@Tag(name = "Conductores", description = "Gestion de conductores de la flota")
public class ConductorController {

    private final ConductorService service;

    public ConductorController(ConductorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los conductores")
    public List<Conductor> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conductor por ID")
    public ResponseEntity<Conductor> buscarPorId(@PathVariable int id) {
        return service.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo conductor",
               description = "La matricula debe tener formato 1234ABC")
    public ResponseEntity<String> registrar(@RequestBody Conductor conductor) {
        if (service.registrar(conductor))
            return ResponseEntity.ok("Conductor registrado correctamente.");
        return ResponseEntity.badRequest()
            .body("Error: matricula invalida, en blanco, o ya existe.");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar el nombre de un conductor")
    public ResponseEntity<String> editar(@PathVariable int id,
                                          @RequestParam String nuevoNombre) {
        if (service.editar(id, nuevoNombre))
            return ResponseEntity.ok("Conductor actualizado correctamente.");
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un conductor por ID")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (service.eliminar(id))
            return ResponseEntity.ok("Conductor eliminado correctamente.");
        return ResponseEntity.notFound().build();
    }
}
