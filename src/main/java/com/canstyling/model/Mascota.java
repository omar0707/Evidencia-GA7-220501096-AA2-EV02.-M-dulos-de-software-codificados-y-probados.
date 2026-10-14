package com.canstyling.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Modelo para las Mascotas / Pacientes Caninos.
 */
public class Mascota {

    private Integer idMascota;
    private Integer idCliente;
    private Integer idRaza;
    private String nombre;
    private String genero; // MACHO, HEMBRA
    private Integer edadAnos;
    private BigDecimal pesoKg;
    private String color;
    private String alergiasCondiciones;
    private boolean agresivo;
    private String estado;
    private Timestamp creadoEn;
    private Timestamp actualizadoEn;

    // Relaciones auxiliares para vistas
    private String nombreCliente;
    private String nombreRaza;

    public Mascota() {
        this.estado = "ACTIVO";
    }

    public Mascota(Integer idCliente, Integer idRaza, String nombre, String genero, Integer edadAnos, BigDecimal pesoKg, String color) {
        this.idCliente = idCliente;
        this.idRaza = idRaza;
        this.nombre = nombre;
        this.genero = genero;
        this.edadAnos = edadAnos;
        this.pesoKg = pesoKg;
        this.color = color;
        this.estado = "ACTIVO";
        this.agresivo = false;
    }

    public Integer getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(Integer idMascota) {
        this.idMascota = idMascota;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(Integer idRaza) {
        this.idRaza = idRaza;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getEdadAnos() {
        return edadAnos;
    }

    public void setEdadAnos(Integer edadAnos) {
        this.edadAnos = edadAnos;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAlergiasCondiciones() {
        return alergiasCondiciones;
    }

    public void setAlergiasCondiciones(String alergiasCondiciones) {
        this.alergiasCondiciones = alergiasCondiciones;
    }

    public boolean isAgresivo() {
        return agresivo;
    }

    public void setAgresivo(boolean agresivo) {
        this.agresivo = agresivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Timestamp creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Timestamp getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Timestamp actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreRaza() {
        return nombreRaza;
    }

    public void setNombreRaza(String nombreRaza) {
        this.nombreRaza = nombreRaza;
    }

    @Override
    public String toString() {
        return "Mascota{" +
                "idMascota=" + idMascota +
                ", nombre='" + nombre + '\'' +
                ", raza='" + (nombreRaza != null ? nombreRaza : idRaza) + '\'' +
                ", genero='" + genero + '\'' +
                ", edad=" + edadAnos + " años" +
                ", peso=" + pesoKg + " kg" +
                '}';
    }
}
