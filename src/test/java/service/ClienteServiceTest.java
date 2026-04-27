package com.apptaxis.api.service;



import com.apptaxis.api.model.Cliente;
import com.apptaxis.api.model.Viaje;
import com.apptaxis.api.repository.ClienteRepository;
import com.apptaxis.api.repository.ViajeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepo;

    @Mock
    private ViajeRepository viajeRepo;

    private ClienteService service;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        service = new ClienteService(clienteRepo, viajeRepo);
        adminId = UUID.randomUUID();
    }

    @Test
    void listarTodos_y_buscarPorId_deleganEnRepositorio() {
        Cliente cliente = new Cliente();
        cliente.setId(10);
        when(clienteRepo.findByAdminId(adminId)).thenReturn(List.of(cliente));
        when(clienteRepo.findByIdAndAdminId(10, adminId)).thenReturn(Optional.of(cliente));

        assertEquals(1, service.listarTodos(adminId).size());
        assertTrue(service.buscarPorId(10, adminId).isPresent());
    }

    @Test
    void buscar_sinQueryUsaFindByAdminId_conQueryUsaBuscar() {
        when(clienteRepo.findByAdminId(adminId)).thenReturn(List.of());
        when(clienteRepo.buscar("ana", adminId)).thenReturn(List.of(new Cliente()));

        assertEquals(0, service.buscar(null, adminId).size());
        assertEquals(0, service.buscar("   ", adminId).size());
        assertEquals(1, service.buscar("  ana  ", adminId).size());

        verify(clienteRepo, times(2)).findByAdminId(adminId);
        verify(clienteRepo).buscar("ana", adminId);
    }

    @Test
    void registrar_fallaSiFaltanCamposObligatorios() {
        Cliente cliente = new Cliente();

        assertTrue(service.registrar(cliente, adminId).isEmpty());

        cliente.setNombre("Ana");
        assertTrue(service.registrar(cliente, adminId).isEmpty());

        cliente.setNombre(" ");
        cliente.setTelefono("666555444");
        assertTrue(service.registrar(cliente, adminId).isEmpty());

        verify(clienteRepo, never()).save(any(Cliente.class));
    }

    @Test
    void registrar_asignaAdminYGuardaCuandoDatosValidos() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Ana");
        cliente.setTelefono("666555444");
        when(clienteRepo.save(cliente)).thenReturn(cliente);

        Optional<Cliente> guardado = service.registrar(cliente, adminId);

        assertTrue(guardado.isPresent());
        assertEquals(adminId, cliente.getAdminId());
        verify(clienteRepo).save(cliente);
    }

    @Test
    void editar_actualizaTodosLosCamposEditables() {
        Cliente original = new Cliente();
        original.setId(1);
        original.setNombre("Nombre viejo");
        original.setTelefono("111");
        original.setEmail("viejo@email.com");
        original.setNotas("nota vieja");

        Cliente datos = new Cliente();
        datos.setNombre("  Nombre nuevo  ");
        datos.setTelefono(" 222 ");
        datos.setEmail("  nuevo@email.com ");
        datos.setNotas("  nota nueva  ");

        when(clienteRepo.findByIdAndAdminId(1, adminId)).thenReturn(Optional.of(original));
        when(clienteRepo.save(original)).thenReturn(original);

        Optional<Cliente> editado = service.editar(1, datos, adminId);

        assertTrue(editado.isPresent());
        assertEquals("Nombre nuevo", original.getNombre());
        assertEquals("222", original.getTelefono());
        assertEquals("nuevo@email.com", original.getEmail());
        assertEquals("nota nueva", original.getNotas());
        verify(clienteRepo).save(original);
    }

    @Test
    void eliminar_y_historialViajes_funcionanCorrectamente() {
        Cliente cliente = new Cliente();
        Viaje viaje = new Viaje();

        when(clienteRepo.findByIdAndAdminId(3, adminId)).thenReturn(Optional.of(cliente));
        when(viajeRepo.findByClienteAndAdminId(3, adminId)).thenReturn(List.of(viaje));

        assertTrue(service.eliminar(3, adminId));
        assertEquals(1, service.historialViajes(3, adminId).size());

        verify(clienteRepo).delete(cliente);
    }
}