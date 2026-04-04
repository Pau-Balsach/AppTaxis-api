package com.apptaxis.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conductores")
public class Conductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String matricula;
    private String nombre;

    @Column(name = "cond_admin", columnDefinition = "uuid")
    private UUID cond_admin;

    @OneToMany(mappedBy = "conductor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Viaje> listaViajes = new ArrayList<>();

    public Conductor() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public UUID getCond_admin() { return cond_admin; }
    public void setCond_admin(UUID cond_admin) { this.cond_admin = cond_admin; }

    public List<Viaje> getListaViajes() { return listaViajes; }
    public void setListaViajes(List<Viaje> listaViajes) { this.listaViajes = listaViajes; }
}
