package com.apptaxis.api.service;

import com.apptaxis.api.model.Cliente;
import com.apptaxis.api.model.Viaje;
import com.apptaxis.api.repository.ClienteRepository;
import com.apptaxis.api.repository.ViajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;
    private final ViajeRepository   viajeRepo;

    public ClienteService(ClienteRepository clienteRepo, ViajeRepository viajeRepo) {
        this.clienteRepo = clienteRepo;
        this.viajeRepo   = viajeRepo;
    }

    public List<Cliente> listarTodos(UUID adminId) {
        return clienteRepo.findByAdminId(adminId);
    }

    public Optional<Cliente> buscarPorId(int id, UUID adminId) {
        return clienteRepo.findByIdAndAdminId(id, adminId);
    }

    public List<Cliente> buscar(String q, UUID adminId) {
        if (q == null || q.isBlank()) return clienteRepo.findByAdminId(adminId);
        return clienteRepo.buscar(q.trim(), adminId);
    }

    public Optional<Cliente> registrar(Cliente cliente, UUID adminId) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) return Optional.empty();
        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) return Optional.empty();
        cliente.setAdminId(adminId);
        return Optional.of(clienteRepo.save(cliente));
    }

    public Optional<Cliente> editar(int id, Cliente datos, UUID adminId) {
        return clienteRepo.findByIdAndAdminId(id, adminId).map(c -> {
            if (datos.getNombre()   != null && !datos.getNombre().isBlank())
                c.setNombre(datos.getNombre().trim());
            if (datos.getTelefono() != null && !datos.getTelefono().isBlank())
                c.setTelefono(datos.getTelefono().trim());
            if (datos.getEmail()    != null) c.setEmail(datos.getEmail().trim());
            if (datos.getNotas()    != null) c.setNotas(datos.getNotas().trim());
            return clienteRepo.save(c);
        });
    }

    public boolean eliminar(int id, UUID adminId) {
        return clienteRepo.findByIdAndAdminId(id, adminId).map(c -> {
            clienteRepo.delete(c);
            return true;
        }).orElse(false);
    }

    public List<Viaje> historialViajes(int clienteId, UUID adminId) {
        return viajeRepo.findByClienteAndAdminId(clienteId, adminId);
    }
}