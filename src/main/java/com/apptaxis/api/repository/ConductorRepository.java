package com.apptaxis.api.repository;

import com.apptaxis.api.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, Integer> {
    boolean existsByMatricula(String matricula);
}
