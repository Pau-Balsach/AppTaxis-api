package com.apptaxis.api.repository;

import com.apptaxis.api.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Integer> {

    @Query("SELECT c FROM Conductor c WHERE c.cond_admin = :adminId")
    List<Conductor> findByAdminId(@Param("adminId") UUID adminId);

    @Query("SELECT COUNT(c) > 0 FROM Conductor c WHERE c.matricula = :matricula AND c.cond_admin = :adminId")
    boolean existsByMatriculaAndAdminId(@Param("matricula") String matricula, @Param("adminId") UUID adminId);

    @Query("SELECT c FROM Conductor c WHERE c.id = :id AND c.cond_admin = :adminId")
    Optional<Conductor> findByIdAndAdminId(@Param("id") int id, @Param("adminId") UUID adminId);
}