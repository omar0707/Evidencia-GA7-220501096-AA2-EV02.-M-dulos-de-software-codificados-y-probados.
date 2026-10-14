package com.canstyling.model;

import java.sql.Timestamp;

/**
 * Modelo para el registro fotografico del servicio (Antes y Despues).
 */
public class FotoMascota {

    private Integer idFoto;
    private Integer idCita;
    private String tipo; // ANTES, DESPUES
    private String urlFoto;
    private String cloudinaryPublicId;
    private Long tamanoBytes;
    private String mimeType;
    private Timestamp subidoEn;

    public FotoMascota() {
    }

    public FotoMascota(Integer idCita, String tipo, String urlFoto, String cloudinaryPublicId, Long tamanoBytes, String mimeType) {
        this.idCita = idCita;
        this.tipo = tipo;
        this.urlFoto = urlFoto;
        this.cloudinaryPublicId = cloudinaryPublicId;
        this.tamanoBytes = tamanoBytes;
        this.mimeType = mimeType;
    }

    public Integer getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getCloudinaryPublicId() {
        return cloudinaryPublicId;
    }

    public void setCloudinaryPublicId(String cloudinaryPublicId) {
        this.cloudinaryPublicId = cloudinaryPublicId;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Timestamp getSubidoEn() {
        return subidoEn;
    }

    public void setSubidoEn(Timestamp subidoEn) {
        this.subidoEn = subidoEn;
    }

    @Override
    public String toString() {
        return "FotoMascota{" +
                "id=" + idFoto +
                ", cita=" + idCita +
                ", tipo='" + tipo + '\'' +
                ", url='" + urlFoto + '\'' +
                '}';
    }
}
