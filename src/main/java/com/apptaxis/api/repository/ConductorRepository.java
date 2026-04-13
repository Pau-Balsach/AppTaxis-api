package com.apptaxis.api.repository;

import com.apptaxis.api.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Integer> {

    /** Solo conductores del admin autenticado. */
    List<Conductor> findByCond_admin(UUID adminId);

    /** Comprueba duplicado de matrícula dentro del mismo admin. */
    boolean existsByMatriculaAndCond_admin(String matricula, UUID adminId);

    /** Busca un conductor por id solo si pertenece al admin. */
    Optional<Conductor> findByIdAndCond_admin(int id, UUID adminId);
}