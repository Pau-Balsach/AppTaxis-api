package com.apptaxis.api.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String telefono;
    private String email;
    private String notas;

    @Column(name = "admin_id", columnDefinition = "uuid")
    private UUID adminId;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Viaje> listaViajes = new ArrayList<>();

    public Cliente() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public UUID getAdminId() { return adminId; }
    public void setAdminId(UUID adminId) { this.adminId = adminId; }

    public List<Viaje> getListaViajes() { return listaViajes; }
    public void setListaViajes(List<Viaje> listaViajes) { this.listaViajes = listaViajes; }
}