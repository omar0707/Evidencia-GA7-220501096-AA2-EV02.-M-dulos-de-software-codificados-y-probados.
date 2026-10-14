package com.canstyling.model;

import java.math.BigDecimal;

/**
 * Modelo para Servicios de Peluqueria y Spa Canino.
 */
public class Servicio {

    private Integer idServicio;
    private String nombreServicio;
    private String descripcion;
    private BigDecimal precioBase;
    private Integer duracionMinutos;
    private String estado;

    public Servicio() {
    }

    public Servicio(Integer idServicio, String nombreServicio, BigDecimal precioBase, Integer duracionMinutos) {
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.precioBase = precioBase;
        this.duracionMinutos = duracionMinutos;
        this.estado = "ACTIVO";
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombreServicio + " ($" + (precioBase != null ? precioBase.toString() : "0") + " - " + duracionMinutos + " min)";
    }
}
