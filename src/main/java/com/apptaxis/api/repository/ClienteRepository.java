package com.apptaxis.api.repository;

import com.apptaxis.api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    @Query("SELECT c FROM Cliente c WHERE c.adminId = :adminId ORDER BY c.nombre ASC")
    List<Cliente> findByAdminId(@Param("adminId") UUID adminId);

    @Query("SELECT c FROM Cliente c WHERE c.id = :id AND c.adminId = :adminId")
    Optional<Cliente> findByIdAndAdminId(@Param("id") int id, @Param("adminId") UUID adminId);

    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.telefono = :telefono AND c.adminId = :adminId")
    boolean existsByTelefonoAndAdminId(@Param("telefono") String telefono, @Param("adminId") UUID adminId);

    @Query("SELECT c FROM Cliente c WHERE c.adminId = :adminId AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR c.telefono LIKE CONCAT('%', :q, '%'))")
    List<Cliente> buscar(@Param("q") String q, @Param("adminId") UUID adminId);
}