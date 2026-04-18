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
        System.out.println("[ClienteService] registrar() — nombre: " + cliente.getNombre()
            + " | telefono: " + cliente.getTelefono()
            + " | adminId: " + adminId);

        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            System.out.println("[ClienteService] BLOQUEADO: nombre vacío o null");
            return Optional.empty();
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            System.out.println("[ClienteService] BLOQUEADO: teléfono vacío o null");
            return Optional.empty();
        }

        cliente.setAdminId(adminId);
        System.out.println("[ClienteService] adminId asignado: " + cliente.getAdminId());

        try {
            Cliente guardado = clienteRepo.save(cliente);
            System.out.println("[ClienteService] cliente guardado con id: " + guardado.getId());
            return Optional.of(guardado);
        } catch (Exception e) {
            System.err.println("[ClienteService] ERROR al guardar: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Cliente> editar(int id, Cliente datos, UUID adminId) {
        System.out.println("[ClienteService] editar() — id: " + id + " | adminId: " + adminId);
        return clienteRepo.findByIdAndAdminId(id, adminId).map(c -> {
            if (datos.getNombre()   != null && !datos.getNombre().isBlank())
                c.setNombre(datos.getNombre().trim());
            if (datos.getTelefono() != null && !datos.getTelefono().isBlank())
                c.setTelefono(datos.getTelefono().trim());
            if (datos.getEmail()    != null) c.setEmail(datos.getEmail().trim());
            if (datos.getNotas()    != null) c.setNotas(datos.getNotas().trim());
            Cliente guardado = clienteRepo.save(c);
            System.out.println("[ClienteService] cliente editado: " + guardado.getNombre());
            return guardado;
        });
    }

    public boolean eliminar(int id, UUID adminId) {
        System.out.println("[ClienteService] eliminar() — id: " + id + " | adminId: " + adminId);
        return clienteRepo.findByIdAndAdminId(id, adminId).map(c -> {
            clienteRepo.delete(c);
            System.out.println("[ClienteService] cliente eliminado correctamente.");
            return true;
        }).orElse(false);
    }

    public List<Viaje> historialViajes(int clienteId, UUID adminId) {
        System.out.println("[ClienteService] historialViajes() — clienteId: " + clienteId);
        return viajeRepo.findByClienteAndAdminId(clienteId, adminId);
    }
}