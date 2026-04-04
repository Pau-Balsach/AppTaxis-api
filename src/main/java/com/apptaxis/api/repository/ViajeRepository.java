package com.apptaxis.api.repository;

import com.apptaxis.api.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, UUID> {
    List<Viaje> findByConductorIdOrderByDiaAscHoraAsc(int conductorId);
}
