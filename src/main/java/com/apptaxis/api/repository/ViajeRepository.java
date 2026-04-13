package com.apptaxis.api.repository;

import com.apptaxis.api.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, UUID> {

    /** Viajes de un conductor, solo si pertenece al admin. */
    List<Viaje> findByConductorIdAndConductorCond_adminOrderByDiaAscHoraAsc(int conductorId, UUID adminId);

    /** Todos los viajes del admin (a través de los conductores que le pertenecen). */
    List<Viaje> findByConductorCond_admin(UUID adminId);

    /** Busca viaje por UUID solo si su conductor pertenece al admin. */
    Optional<Viaje> findByIdAndConductorCond_admin(UUID id, UUID adminId);
}