package com.canstyling.model;

import java.sql.Timestamp;

/**
 * Modelo para soporte de multiples numeros telefonicos por cliente (Movil, WhatsApp, Fijo).
 */
public class ClienteTelefono {

    private Integer idTelefono;
    private Integer idCliente;
    private String numeroTelefono;
    private String tipo; // WHATSAPP, MOVIL, FIJO, OTRO
    private boolean esPrincipal;
    private boolean verificado;
    private Timestamp creadoEn;

    public ClienteTelefono() {
    }

    public ClienteTelefono(Integer idCliente, String numeroTelefono, String tipo, boolean esPrincipal, boolean verificado) {
        this.idCliente = idCliente;
        this.numeroTelefono = numeroTelefono;
        this.tipo = tipo != null ? tipo : "WHATSAPP";
        this.esPrincipal = esPrincipal;
        this.verificado = verificado;
    }

    public ClienteTelefono(String numeroTelefono, String tipo, boolean esPrincipal) {
        this.numeroTelefono = numeroTelefono;
        this.tipo = tipo;
        this.esPrincipal = esPrincipal;
        this.verificado = false;
    }

    public Integer getIdTelefono() {
        return idTelefono;
    }

    public void setIdTelefono(Integer idTelefono) {
        this.idTelefono = idTelefono;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public Timestamp getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Timestamp creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public String toString() {
        return "ClienteTelefono{" +
                "idTelefono=" + idTelefono +
                ", numero='" + numeroTelefono + '\'' +
                ", tipo='" + tipo + '\'' +
                ", principal=" + esPrincipal +
                ", verificado=" + verificado +
                '}';
    }
}
