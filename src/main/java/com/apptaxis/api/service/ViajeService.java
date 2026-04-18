package com.apptaxis.api.service;

import com.apptaxis.api.model.Conductor;
import com.apptaxis.api.model.Viaje;
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

    public ViajeService(ViajeRepository viajeRepo, ConductorRepository conductorRepo) {
        this.viajeRepo     = viajeRepo;
        this.conductorRepo = conductorRepo;
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

    public boolean crear(Viaje viaje, int conductorId, UUID adminId) {
        Optional<Conductor> conductor = conductorRepo.findByIdAndAdminId(conductorId, adminId);
        if (conductor.isEmpty()) return false;
        viaje.setConductor(conductor.get());
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