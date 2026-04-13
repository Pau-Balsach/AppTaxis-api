package com.apptaxis.api.repository;

import com.apptaxis.api.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Integer> {

    /** Busca una API key activa por su hash SHA-256. */
    Optional<ApiKey> findByKeyHashAndActivaTrue(String keyHash);
}