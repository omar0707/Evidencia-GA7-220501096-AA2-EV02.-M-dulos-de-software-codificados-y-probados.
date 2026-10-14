package com.canstyling.model;

import java.sql.Timestamp;

/**
 * Modelo de Codigo OTP para autenticacion segura sin contraseña y confirmacion de turnos.
 */
public class CodigoOTP {

    private Integer idOtp;
    private String destino; // Correo electronico o telefono
    private String codigoHash; // Hash SHA-256 del codigo
    private String tipoProposito; // LOGIN, REGISTRO, VERIFICACION_WHATSAPP, CONFIRMAR_CITA
    private int intentosFallidos;
    private boolean usado;
    private Timestamp expiraEn;
    private Timestamp creadoEn;

    // Campo transitorio en memoria (solo visible al momento de generar el codigo)
    private transient String codigoPlano;

    public CodigoOTP() {
        this.intentosFallidos = 0;
        this.usado = false;
    }

    public CodigoOTP(String destino, String codigoHash, String tipoProposito, Timestamp expiraEn) {
        this.destino = destino;
        this.codigoHash = codigoHash;
        this.tipoProposito = tipoProposito != null ? tipoProposito : "LOGIN";
        this.expiraEn = expiraEn;
        this.intentosFallidos = 0;
        this.usado = false;
    }

    public Integer getIdOtp() {
        return idOtp;
    }

    public void setIdOtp(Integer idOtp) {
        this.idOtp = idOtp;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getCodigoHash() {
        return codigoHash;
    }

    public void setCodigoHash(String codigoHash) {
        this.codigoHash = codigoHash;
    }

    public String getTipoProposito() {
        return tipoProposito;
    }

    public void setTipoProposito(String tipoProposito) {
        this.tipoProposito = tipoProposito;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

    public Timestamp getExpiraEn() {
        return expiraEn;
    }

    public void setExpiraEn(Timestamp expiraEn) {
        this.expiraEn = expiraEn;
    }

    public Timestamp getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Timestamp creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getCodigoPlano() {
        return codigoPlano;
    }

    public void setCodigoPlano(String codigoPlano) {
        this.codigoPlano = codigoPlano;
    }

    public boolean estaExpirado() {
        if (expiraEn == null) return true;
        return System.currentTimeMillis() > expiraEn.getTime();
    }

    @Override
    public String toString() {
        return "CodigoOTP{" +
                "id=" + idOtp +
                ", destino='" + destino + '\'' +
                ", proposito='" + tipoProposito + '\'' +
                ", usado=" + usado +
                ", intentos=" + intentosFallidos +
                ", expira=" + expiraEn +
                '}';
    }
}
