package com.apptaxis.api.repository;

import com.apptaxis.api.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, UUID> {

    @Query("SELECT v FROM Viaje v WHERE v.conductor.id = :conductorId AND v.conductor.cond_admin = :adminId ORDER BY v.dia ASC, v.hora ASC")
    List<Viaje> findByConductorAndAdminId(@Param("conductorId") int conductorId, @Param("adminId") UUID adminId);

    @Query("SELECT v FROM Viaje v WHERE v.conductor.cond_admin = :adminId")
    List<Viaje> findAllByAdminId(@Param("adminId") UUID adminId);

    @Query("SELECT v FROM Viaje v WHERE v.id = :id AND v.conductor.cond_admin = :adminId")
    Optional<Viaje> findByIdAndAdminId(@Param("id") UUID id, @Param("adminId") UUID adminId);

    @Query("SELECT v FROM Viaje v WHERE v.cliente.id = :clienteId AND v.conductor.cond_admin = :adminId ORDER BY v.dia DESC, v.hora DESC")
    List<Viaje> findByClienteAndAdminId(@Param("clienteId") int clienteId, @Param("adminId") UUID adminId);
}