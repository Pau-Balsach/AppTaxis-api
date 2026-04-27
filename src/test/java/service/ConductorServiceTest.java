package com.apptaxis.api.service;

import com.apptaxis.api.model.Conductor;
import com.apptaxis.api.repository.ConductorRepository;
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
class ConductorServiceTest {

    @Mock
    private ConductorRepository repo;

    private ConductorService service;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        service = new ConductorService(repo);
        adminId = UUID.randomUUID();
    }

    @Test
    void listarTodos_devuelveConductoresDelAdmin() {
        Conductor conductor = new Conductor();
        conductor.setId(1);
        when(repo.findByAdminId(adminId)).thenReturn(List.of(conductor));

        List<Conductor> resultado = service.listarTodos(adminId);

        assertEquals(1, resultado.size());
        verify(repo).findByAdminId(adminId);
    }

    @Test
    void buscarPorId_devuelveConductorSiExiste() {
        Conductor conductor = new Conductor();
        conductor.setId(2);
        when(repo.findByIdAndAdminId(2, adminId)).thenReturn(Optional.of(conductor));

        Optional<Conductor> resultado = service.buscarPorId(2, adminId);

        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().getId());
    }

    @Test
    void registrar_fallaSiMatriculaInvalidaONombreVacioODuplicada() {
        Conductor conductor = new Conductor();
        conductor.setMatricula("ABC");
        conductor.setNombre("Pepe");

        assertFalse(service.registrar(conductor, adminId));

        conductor.setMatricula("1234ABC");
        conductor.setNombre(" ");
        assertFalse(service.registrar(conductor, adminId));

        conductor.setNombre("Pepe");
        when(repo.existsByMatriculaAndAdminId("1234ABC", adminId)).thenReturn(true);
        assertFalse(service.registrar(conductor, adminId));

        verify(repo, never()).save(any(Conductor.class));
    }

    @Test
    void registrar_guardaConAdminCuandoDatosValidos() {
        Conductor conductor = new Conductor();
        conductor.setMatricula("1234ABC");
        conductor.setNombre("Conductor Uno");
        when(repo.existsByMatriculaAndAdminId("1234ABC", adminId)).thenReturn(false);

        boolean creado = service.registrar(conductor, adminId);

        assertTrue(creado);
        assertEquals(adminId, conductor.getCond_admin());
        verify(repo).save(conductor);
    }

    @Test
    void editar_actualizaNombreSiExisteYNombreValido() {
        Conductor conductor = new Conductor();
        conductor.setNombre("Viejo");
        when(repo.findByIdAndAdminId(1, adminId)).thenReturn(Optional.of(conductor));

        boolean editado = service.editar(1, "  Nuevo Nombre  ", adminId);

        assertTrue(editado);
        assertEquals("Nuevo Nombre", conductor.getNombre());
        verify(repo).save(conductor);
    }

    @Test
    void editar_fallaSiNombreInvalidoONoExiste() {
        assertFalse(service.editar(1, " ", adminId));
        when(repo.findByIdAndAdminId(1, adminId)).thenReturn(Optional.empty());
        assertFalse(service.editar(1, "Nombre", adminId));
    }

    @Test
    void eliminar_eliminaSiExiste() {
        Conductor conductor = new Conductor();
        when(repo.findByIdAndAdminId(4, adminId)).thenReturn(Optional.of(conductor));

        boolean eliminado = service.eliminar(4, adminId);

        assertTrue(eliminado);
        verify(repo).delete(conductor);
    }

    @Test
    void eliminar_devuelveFalseSiNoExiste() {
        when(repo.findByIdAndAdminId(99, adminId)).thenReturn(Optional.empty());

        boolean eliminado = service.eliminar(99, adminId);

        assertFalse(eliminado);
        verify(repo, never()).delete(any(Conductor.class));
    }
}