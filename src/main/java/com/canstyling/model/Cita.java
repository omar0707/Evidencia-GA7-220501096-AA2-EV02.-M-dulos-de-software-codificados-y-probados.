package com.canstyling.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para las Citas y Turnos de Peluqueria Canina.
 * Incorpora el campo 'tokenAcceso' (UUID) como medida de seguridad Anti-IDOR.
 */
public class Cita {

    private Integer idCita;
    private Integer idCliente;
    private Integer idMascota;
    private Integer idServicio;
    private Integer idPeluquero;
    private Timestamp fechaHora;
    private String estado; // PENDIENTE, CONFIRMADA, EN_PROCESO, COMPLETADA, CANCELADA
    private BigDecimal valorTotal;
    private BigDecimal descuentoAplicado;
    private String tokenAcceso; // Token Anti-IDOR
    private String observacionesRecepcion;
    private String observacionesEntrega;
    private Timestamp creadoEn;
    private Timestamp actualizadoEn;

    // Relaciones auxiliares para vistas y reportes
    private String nombreCliente;
    private String telefonoCliente;
    private String nombreMascota;
    private String nombreServicio;
    private String nombrePeluquero;
    private List<FotoMascota> fotos = new ArrayList<>();

    public Cita() {
        this.estado = "PENDIENTE";
        this.descuentoAplicado = BigDecimal.ZERO;
    }

    public Cita(Integer idCliente, Integer idMascota, Integer idServicio, Timestamp fechaHora, BigDecimal valorTotal, String tokenAcceso) {
        this.idCliente = idCliente;
        this.idMascota = idMascota;
        this.idServicio = idServicio;
        this.fechaHora = fechaHora;
        this.valorTotal = valorTotal;
        this.descuentoAplicado = BigDecimal.ZERO;
        this.tokenAcceso = tokenAcceso;
        this.estado = "PENDIENTE";
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(Integer idMascota) {
        this.idMascota = idMascota;
    }

    public Integer getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(Integer idServicio) {
        this.idServicio = idServicio;
    }

    public Integer getIdPeluquero() {
        return idPeluquero;
    }

    public void setIdPeluquero(Integer idPeluquero) {
        this.idPeluquero = idPeluquero;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(BigDecimal descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }

    public String getTokenAcceso() {
        return tokenAcceso;
    }

    public void setTokenAcceso(String tokenAcceso) {
        this.tokenAcceso = tokenAcceso;
    }

    public String getObservacionesRecepcion() {
        return observacionesRecepcion;
    }

    public void setObservacionesRecepcion(String observacionesRecepcion) {
        this.observacionesRecepcion = observacionesRecepcion;
    }

    public String getObservacionesEntrega() {
        return observacionesEntrega;
    }

    public void setObservacionesEntrega(String observacionesEntrega) {
        this.observacionesEntrega = observacionesEntrega;
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

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio) {
        this.nombreServicio = nombreServicio;
    }

    public String getNombrePeluquero() {
        return nombrePeluquero;
    }

    public void setNombrePeluquero(String nombrePeluquero) {
        this.nombrePeluquero = nombrePeluquero;
    }

    public List<FotoMascota> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoMascota> fotos) {
        this.fotos = fotos;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "id=" + idCita +
                ", fecha=" + fechaHora +
                ", mascota='" + (nombreMascota != null ? nombreMascota : idMascota) + '\'' +
                ", servicio='" + (nombreServicio != null ? nombreServicio : idServicio) + '\'' +
                ", valor=$" + valorTotal +
                ", estado='" + estado + '\'' +
                ", tokenAntiIdor='" + tokenAcceso + '\'' +
                '}';
    }
}
