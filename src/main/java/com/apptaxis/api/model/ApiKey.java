package com.apptaxis.api.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "key_hash")
    private String keyHash;

    private boolean activa;

    @Column(name = "admin_id", columnDefinition = "uuid")
    private UUID adminId;

    public Integer getId()            { return id; }
    public String getNombreCliente()  { return nombreCliente; }
    public String getKeyHash()        { return keyHash; }
    public boolean isActiva()         { return activa; }
    public UUID getAdminId()          { return adminId; }

    public void setId(Integer id)                  { this.id = id; }
    public void setNombreCliente(String v)         { this.nombreCliente = v; }
    public void setKeyHash(String v)               { this.keyHash = v; }
    public void setActiva(boolean v)               { this.activa = v; }
    public void setAdminId(UUID adminId)           { this.adminId = adminId; }
}