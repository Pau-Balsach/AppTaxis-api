package com.apptaxis.api.service;

import com.apptaxis.api.model.Conductor;
import com.apptaxis.api.repository.ConductorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConductorService {

    private static final String REGEX_MATRICULA = "^[0-9]{4}[A-Z]{3}$";

    private final ConductorRepository repo;

    public ConductorService(ConductorRepository repo) {
        this.repo = repo;
    }

    public List<Conductor> listarTodos() {
        return repo.findAll();
    }

    public Optional<Conductor> buscarPorId(int id) {
        return repo.findById(id);
    }

    public boolean registrar(Conductor conductor) {
        if (conductor.getMatricula() == null || !conductor.getMatricula().matches(REGEX_MATRICULA))
            return false;
        if (conductor.getNombre() == null || conductor.getNombre().isBlank())
            return false;
        if (repo.existsByMatricula(conductor.getMatricula()))
            return false;
        repo.save(conductor);
        return true;
    }

    public boolean editar(int id, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isBlank()) return false;
        return repo.findById(id).map(c -> {
            c.setNombre(nuevoNombre.trim());
            repo.save(c);
            return true;
        }).orElse(false);
    }

    public boolean eliminar(int id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        return true;
    }
}
