package com.apptaxis.api.service;

import com.apptaxis.api.model.Cliente;
import com.apptaxis.api.model.Conductor;
import com.apptaxis.api.model.Viaje;
import com.apptaxis.api.repository.ClienteRepository;
import com.apptaxis.api.repository.ConductorRepository;
import com.apptaxis.api.repository.ViajeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViajeServiceTest {

    @Mock
    private ViajeRepository viajeRepo;

    @Mock
    private ConductorRepository conductorRepo;

    @Mock
    private ClienteRepository clienteRepo;

    private ViajeService service;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        service = new ViajeService(viajeRepo, conductorRepo, clienteRepo);
        adminId = UUID.randomUUID();
    }

    @Test
    void listarYBuscar_deleganEnRepositorio() {
        Viaje viaje = new Viaje();
        when(viajeRepo.findAllByAdminId(adminId)).thenReturn(List.of(viaje));
        when(viajeRepo.findByConductorAndAdminId(7, adminId)).thenReturn(List.of(viaje));
        when(viajeRepo.findByIdAndAdminId(viaje.getId(), adminId)).thenReturn(Optional.of(viaje));

        assertEquals(1, service.listarTodos(adminId).size());
        assertEquals(1, service.listarPorConductor(7, adminId).size());
        assertTrue(service.buscarPorId(viaje.getId(), adminId).isPresent());
    }

    @Test
    void crear_fallaSiConductorNoExiste() {
        Viaje viaje = new Viaje();
        when(conductorRepo.findByIdAndAdminId(1, adminId)).thenReturn(Optional.empty());

        boolean creado = service.crear(viaje, 1, adminId);

        assertFalse(creado);
        verify(viajeRepo, never()).save(any(Viaje.class));
    }

    @Test
    void crear_asignaConductorDiaFinYTelefonoClienteSiAplica() {
        Conductor conductor = new Conductor();
        conductor.setId(2);

        Cliente cliente = new Cliente();
        cliente.setId(8);
        cliente.setTelefono("600700800");

        Viaje viaje = new Viaje();
        viaje.setDia(LocalDate.of(2026, 4, 20));
        viaje.setHora(LocalTime.of(9, 30));
        Cliente clientePayload = new Cliente();
        clientePayload.setId(8);
        viaje.setCliente(clientePayload);

        when(conductorRepo.findByIdAndAdminId(2, adminId)).thenReturn(Optional.of(conductor));
        when(clienteRepo.findByIdAndAdminId(8, adminId)).thenReturn(Optional.of(cliente));

        boolean creado = service.crear(viaje, 2, adminId);

        assertTrue(creado);
        assertEquals(conductor, viaje.getConductor());
        assertEquals(viaje.getDia(), viaje.getDiaFin());
        assertEquals("600700800", viaje.getTelefonocliente());
        assertEquals(cliente, viaje.getCliente());
        verify(viajeRepo).save(viaje);
    }

    @Test
    void editar_actualizaTodosLosCamposDelViajeCorrectamente() {
        Conductor conductorOriginal = new Conductor();
        conductorOriginal.setId(1);
        Cliente clienteOriginal = new Cliente();
        clienteOriginal.setId(1);

        Viaje existente = new Viaje();
        UUID viajeId = existente.getId();
        existente.setDia(LocalDate.of(2026, 4, 1));
        existente.setDiaFin(LocalDate.of(2026, 4, 1));
        existente.setHora(LocalTime.of(8, 0));
        existente.setHoraFinalizacion(LocalTime.of(8, 30));
        existente.setPuntorecogida("Origen viejo");
        existente.setPuntodejada("Destino viejo");
        existente.setTelefonocliente("111222333");
        existente.setConductor(conductorOriginal);
        existente.setCliente(clienteOriginal);

        Conductor nuevoConductor = new Conductor();
        nuevoConductor.setId(2);
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setId(3);

        Viaje datos = new Viaje();
        datos.setDia(LocalDate.of(2026, 5, 2));
        datos.setDiaFin(LocalDate.of(2026, 5, 3));
        datos.setHora(LocalTime.of(10, 15));
        datos.setHoraFinalizacion(LocalTime.of(11, 0));
        datos.setPuntorecogida("Origen nuevo");
        datos.setPuntodejada("Destino nuevo");
        datos.setTelefonocliente("999888777");

        Conductor conductorPayload = new Conductor();
        conductorPayload.setId(2);
        datos.setConductor(conductorPayload);

        Cliente clientePayload = new Cliente();
        clientePayload.setId(3);
        datos.setCliente(clientePayload);

        when(viajeRepo.findByIdAndAdminId(viajeId, adminId)).thenReturn(Optional.of(existente));
        when(conductorRepo.findByIdAndAdminId(2, adminId)).thenReturn(Optional.of(nuevoConductor));
        when(clienteRepo.findByIdAndAdminId(3, adminId)).thenReturn(Optional.of(nuevoCliente));

        boolean editado = service.editar(viajeId, datos, adminId);

        assertTrue(editado);
        assertEquals(LocalDate.of(2026, 5, 2), existente.getDia());
        assertEquals(LocalDate.of(2026, 5, 3), existente.getDiaFin());
        assertEquals(LocalTime.of(10, 15), existente.getHora());
        assertEquals(LocalTime.of(11, 0), existente.getHoraFinalizacion());
        assertEquals("Origen nuevo", existente.getPuntorecogida());
        assertEquals("Destino nuevo", existente.getPuntodejada());
        assertEquals("999888777", existente.getTelefonocliente());
        assertEquals(nuevoConductor, existente.getConductor());
        assertEquals(nuevoCliente, existente.getCliente());
        verify(viajeRepo).save(existente);
    }

    @Test
    void editar_siClienteNuloLoDesasocia_y_siNoExisteDevuelveFalse() {
        Viaje existente = new Viaje();
        UUID viajeId = existente.getId();
        Cliente clienteActual = new Cliente();
        clienteActual.setId(10);
        existente.setCliente(clienteActual);

        Viaje datos = new Viaje();
        datos.setDia(LocalDate.of(2026, 6, 1));
        datos.setHora(LocalTime.of(12, 0));
        datos.setHoraFinalizacion(LocalTime.of(12, 30));
        datos.setPuntorecogida("A");
        datos.setPuntodejada("B");
        datos.setTelefonocliente("123");
        datos.setCliente(null);

        when(viajeRepo.findByIdAndAdminId(viajeId, adminId)).thenReturn(Optional.of(existente));
        assertTrue(service.editar(viajeId, datos, adminId));
        assertNull(existente.getCliente());

        UUID otroId = UUID.randomUUID();
        when(viajeRepo.findByIdAndAdminId(otroId, adminId)).thenReturn(Optional.empty());
        assertFalse(service.editar(otroId, datos, adminId));
    }

    @Test
    void eliminar_eliminaSiExiste() {
        Viaje viaje = new Viaje();
        UUID viajeId = viaje.getId();
        when(viajeRepo.findByIdAndAdminId(viajeId, adminId)).thenReturn(Optional.of(viaje));

        boolean eliminado = service.eliminar(viajeId, adminId);

        assertTrue(eliminado);
        verify(viajeRepo).delete(viaje);
    }
}