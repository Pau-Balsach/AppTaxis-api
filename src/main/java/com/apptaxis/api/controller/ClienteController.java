package com.apptaxis.api.controller;

import com.apptaxis.api.model.Cliente;
import com.apptaxis.api.model.Viaje;
import com.apptaxis.api.security.ApiKeyFilter;
import com.apptaxis.api.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Gestión de clientes de la flota")
@SecurityRequirement(name = "apiKey")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    private UUID adminId(HttpServletRequest req) {
        return (UUID) req.getAttribute(ApiKeyFilter.ADMIN_ID_ATTR);
    }

    @GetMapping
    @Operation(summary = "Listar todos los clientes. Opcionalmente filtra por nombre o teléfono con ?q=")
    public List<Cliente> listar(
            @RequestParam(required = false) String q,
            HttpServletRequest req) {
        return service.buscar(q, adminId(req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public ResponseEntity<Cliente> buscarPorId(
            @PathVariable int id,
            HttpServletRequest req) {
        return service.buscarPorId(id, adminId(req))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo cliente")
    public ResponseEntity<Cliente> registrar(
            @RequestBody Cliente cliente,
            HttpServletRequest req) {
        return service.registrar(cliente, adminId(req))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un cliente existente")
    public ResponseEntity<Cliente> editar(
            @PathVariable int id,
            @RequestBody Cliente datos,
            HttpServletRequest req) {
        return service.editar(id, datos, adminId(req))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente por ID")
    public ResponseEntity<String> eliminar(
            @PathVariable int id,
            HttpServletRequest req) {
        if (service.eliminar(id, adminId(req)))
            return ResponseEntity.ok("Cliente eliminado correctamente.");
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/viajes")
    @Operation(summary = "Historial de viajes de un cliente ordenados por fecha desc")
    public ResponseEntity<List<Viaje>> historialViajes(
            @PathVariable int id,
            HttpServletRequest req) {
        return service.buscarPorId(id, adminId(req))
            .map(c -> ResponseEntity.ok(service.historialViajes(id, adminId(req))))
            .orElse(ResponseEntity.notFound().build());
    }
}