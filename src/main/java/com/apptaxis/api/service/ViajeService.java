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

    private final ViajeRepository viajeRepo;
    private final ConductorRepository conductorRepo;

    public ViajeService(ViajeRepository viajeRepo, ConductorRepository conductorRepo) {
        this.viajeRepo     = viajeRepo;
        this.conductorRepo = conductorRepo;
    }

    public List<Viaje> listarTodos() {
        return viajeRepo.findAll();
    }

    public List<Viaje> listarPorConductor(int conductorId) {
        return viajeRepo.findByConductorIdOrderByDiaAscHoraAsc(conductorId);
    }

    public Optional<Viaje> buscarPorId(UUID id) {
        return viajeRepo.findById(id);
    }

    public boolean crear(Viaje viaje, int conductorId) {
        Optional<Conductor> conductor = conductorRepo.findById(conductorId);
        if (conductor.isEmpty()) return false;
        viaje.setConductor(conductor.get());
        viajeRepo.save(viaje);
        return true;
    }

    public boolean editar(UUID id, Viaje datos) {
        return viajeRepo.findById(id).map(v -> {
            v.setDia(datos.getDia());
            v.setHora(datos.getHora());
            v.setPuntorecogida(datos.getPuntorecogida());
            v.setPuntodejada(datos.getPuntodejada());
            v.setTelefonocliente(datos.getTelefonocliente());
            viajeRepo.save(v);
            return true;
        }).orElse(false);
    }

    public boolean eliminar(UUID id) {
        if (!viajeRepo.existsById(id)) return false;
        viajeRepo.deleteById(id);
        return true;
    }
}
