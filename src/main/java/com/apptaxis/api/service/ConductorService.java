package com.apptaxis.api.service;

import com.apptaxis.api.model.Conductor;
import com.apptaxis.api.repository.ConductorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConductorService {

    private static final String REGEX_MATRICULA = "^[0-9]{4}[A-Z]{3}$";

    private final ConductorRepository repo;

    public ConductorService(ConductorRepository repo) {
        this.repo = repo;
    }

    public List<Conductor> listarTodos(UUID adminId) {
        return repo.findByAdminId(adminId);
    }

    public Optional<Conductor> buscarPorId(int id, UUID adminId) {
        return repo.findByIdAndAdminId(id, adminId);
    }

    public boolean registrar(Conductor conductor, UUID adminId) {
        if (conductor.getMatricula() == null || !conductor.getMatricula().matches(REGEX_MATRICULA))
            return false;
        if (conductor.getNombre() == null || conductor.getNombre().isBlank())
            return false;
        if (repo.existsByMatriculaAndAdminId(conductor.getMatricula(), adminId))
            return false;
        conductor.setCond_admin(adminId);
        repo.save(conductor);
        return true;
    }

    public boolean editar(int id, String nuevoNombre, UUID adminId) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) return false;
        return repo.findByIdAndAdminId(id, adminId).map(c -> {
            c.setNombre(nuevoNombre.trim());
            repo.save(c);
            return true;
        }).orElse(false);
    }

    public boolean eliminar(int id, UUID adminId) {
        return repo.findByIdAndAdminId(id, adminId).map(c -> {
            repo.delete(c);
            return true;
        }).orElse(false);
    }
}