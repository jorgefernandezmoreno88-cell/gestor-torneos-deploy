package com.jorge.gestorTorneos.dto.response;

public class TorneoBusquedaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private String formato;
    private String nivel;
    private Double similitud;

    public TorneoBusquedaResponse() {
    }

    public TorneoBusquedaResponse(Long id, String nombre, String descripcion, String formato, String nivel, Double similitud) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.formato = formato;
        this.nivel = nivel;
        this.similitud = similitud;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFormato() {
        return formato;
    }

    public String getNivel() {
        return nivel;
    }

    public Double getSimilitud() {
        return similitud;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public void setSimilitud(Double similitud) {
        this.similitud = similitud;
    }
}