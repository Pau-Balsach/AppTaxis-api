package com.apptaxis.api.service;

import com.apptaxis.api.model.Cliente;
import com.apptaxis.api.model.Conductor;
import com.apptaxis.api.model.Viaje;
import com.apptaxis.api.repository.ClienteRepository;
import com.apptaxis.api.repository.ConductorRepository;
import com.apptaxis.api.repository.ViajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ViajeService {

    private final ViajeRepository     viajeRepo;
    private final ConductorRepository conductorRepo;
    private final ClienteRepository   clienteRepo;

    public ViajeService(ViajeRepository viajeRepo,
                        ConductorRepository conductorRepo,
                        ClienteRepository clienteRepo) {
        this.viajeRepo     = viajeRepo;
        this.conductorRepo = conductorRepo;
        this.clienteRepo   = clienteRepo;
    }

    public List<Viaje> listarTodos(UUID adminId) {
        return viajeRepo.findAllByAdminId(adminId);
    }

    public List<Viaje> listarPorConductor(int conductorId, UUID adminId) {
        return viajeRepo.findByConductorAndAdminId(conductorId, adminId);
    }

    public Optional<Viaje> buscarPorId(UUID id, UUID adminId) {
        return viajeRepo.findByIdAndAdminId(id, adminId);
    }

    /**
     * Crea un viaje asignado a un conductor.
     * Si el body incluye cliente.id, vincula el cliente al viaje
     * y autocompleta telefonocliente con el del cliente.
     */
    public boolean crear(Viaje viaje, int conductorId, UUID adminId) {
        Optional<Conductor> conductor = conductorRepo.findByIdAndAdminId(conductorId, adminId);
        if (conductor.isEmpty()) return false;
        viaje.setConductor(conductor.get());

        // Vincular cliente si viene en el body
        if (viaje.getCliente() != null && viaje.getCliente().getId() != 0) {
            clienteRepo.findByIdAndAdminId(viaje.getCliente().getId(), adminId)
                .ifPresent(c -> {
                    viaje.setCliente(c);
                    // Autocompletar teléfono si no viene en el body
                    if (viaje.getTelefonocliente() == null || viaje.getTelefonocliente().isBlank()) {
                        viaje.setTelefonocliente(c.getTelefono());
                    }
                });
        }

        viajeRepo.save(viaje);
        return true;
    }

    public boolean editar(UUID id, Viaje datos, UUID adminId) {
        return viajeRepo.findByIdAndAdminId(id, adminId).map(v -> {
            v.setDia(datos.getDia());
            v.setHora(datos.getHora());
            v.setHoraFinalizacion(datos.getHoraFinalizacion());
            v.setPuntorecogida(datos.getPuntorecogida());
            v.setPuntodejada(datos.getPuntodejada());
            v.setTelefonocliente(datos.getTelefonocliente());

            // Actualizar cliente si viene en el body
            if (datos.getCliente() != null && datos.getCliente().getId() != 0) {
                clienteRepo.findByIdAndAdminId(datos.getCliente().getId(), adminId)
                    .ifPresent(v::setCliente);
            } else if (datos.getCliente() == null) {
                v.setCliente(null); // permite desvincular el cliente
            }

            viajeRepo.save(v);
            return true;
        }).orElse(false);
    }

    public boolean eliminar(UUID id, UUID adminId) {
        return viajeRepo.findByIdAndAdminId(id, adminId).map(v -> {
            viajeRepo.delete(v);
            return true;
        }).orElse(false);
    }
}