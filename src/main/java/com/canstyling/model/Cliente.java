package com.canstyling.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa a un Cliente / Propietario de Mascota.
 * Cumple con estandares JavaBeans: encapsulamiento, getters/setters y constructores sobrecargados.
 */
public class Cliente {

    private Integer idCliente;
    private Integer idUsuario;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String correo;
    private String direccion;
    private String ciudad;
    private String estado;
    private String observaciones;
    private Timestamp fechaRegistro;
    private Timestamp actualizadoEn;

    // Relacion uno a muchos: multiples telefonos / lineas de WhatsApp
    private List<ClienteTelefono> telefonos = new ArrayList<>();
    // Relacion uno a muchos: mascotas del cliente
    private List<Mascota> mascotas = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(String tipoDocumento, String numeroDocumento, String nombres, String apellidos, String correo, String direccion, String ciudad) {
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.estado = "ACTIVO";
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Timestamp getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Timestamp actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    public List<ClienteTelefono> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<ClienteTelefono> telefonos) {
        this.telefonos = telefonos;
    }

    public void addTelefono(ClienteTelefono telefono) {
        this.telefonos.add(telefono);
    }

    public List<Mascota> getMascotas() {
        return mascotas;
    }

    public void setMascotas(List<Mascota> mascotas) {
        this.mascotas = mascotas;
    }

    public String getTelefonoPrincipal() {
        for (ClienteTelefono tel : telefonos) {
            if (tel.isEsPrincipal()) {
                return tel.getNumeroTelefono();
            }
        }
        return telefonos.isEmpty() ? "Sin teléfono" : telefonos.get(0).getNumeroTelefono();
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", doc='" + tipoDocumento + " " + numeroDocumento + '\'' +
                ", nombre='" + getNombreCompleto() + '\'' +
                ", correo='" + correo + '\'' +
                ", telPrincipal='" + getTelefonoPrincipal() + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
