package com.canstyling.model;

import java.sql.Timestamp;

/**
 * Modelo para Usuarios del Sistema (Administrador, Recepcionista, Peluquero).
 */
public class Usuario {

    private Integer idUsuario;
    private Integer idRol;
    private String nombreRol;
    private String nombres;
    private String apellidos;
    private String correo;
    private String passwordHash;
    private String telefono;
    private String estado; // ACTIVO, INACTIVO, BLOQUEADO
    private Timestamp creadoEn;
    private Timestamp actualizadoEn;

    public Usuario() {
        this.estado = "ACTIVO";
    }

    public Usuario(Integer idRol, String nombres, String apellidos, String correo, String passwordHash, String telefono) {
        this.idRol = idRol;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.estado = "ACTIVO";
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + idUsuario +
                ", nombre='" + getNombreCompleto() + '\'' +
                ", correo='" + correo + '\'' +
                ", rol='" + nombreRol + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
