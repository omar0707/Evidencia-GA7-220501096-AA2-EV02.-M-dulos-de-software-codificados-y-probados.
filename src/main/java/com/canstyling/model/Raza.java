package com.canstyling.model;

/**
 * Modelo de Raza Canina y caracteristicas morfologicas.
 */
public class Raza {

    private Integer idRaza;
    private String nombreRaza;
    private String tamano; // MINI, PEQUENO, MEDIANO, GRANDE, GIGANTE
    private String tipoPelo; // CORTO, MEDIO, LARGO, RIZADO, DOBLE_CAPA
    private String descripcion;

    public Raza() {
    }

    public Raza(Integer idRaza, String nombreRaza, String tamano, String tipoPelo) {
        this.idRaza = idRaza;
        this.nombreRaza = nombreRaza;
        this.tamano = tamano;
        this.tipoPelo = tipoPelo;
    }

    public Integer getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(Integer idRaza) {
        this.idRaza = idRaza;
    }

    public String getNombreRaza() {
        return nombreRaza;
    }

    public void setNombreRaza(String nombreRaza) {
        this.nombreRaza = nombreRaza;
    }

    public String getTamano() {
        return tamano;
    }

    public void setTamano(String tamano) {
        this.tamano = tamano;
    }

    public String getTipoPelo() {
        return tipoPelo;
    }

    public void setTipoPelo(String tipoPelo) {
        this.tipoPelo = tipoPelo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombreRaza + " (" + tamano + " - Pelo " + tipoPelo + ")";
    }
}
