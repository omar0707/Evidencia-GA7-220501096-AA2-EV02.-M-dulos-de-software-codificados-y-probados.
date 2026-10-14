package com.canstyling.service;

import com.canstyling.dao.CitaDAO;
import com.canstyling.dao.impl.CitaDAOImpl;
import com.canstyling.model.Cita;
import com.canstyling.model.FotoMascota;
import com.canstyling.model.Servicio;
import com.canstyling.util.SecurityUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Capa de Servicios y Reglas de Negocio para Citas, Descuentos,
 * Prevencion de Citas Fantasma y Visualizacion de Albumes Digitales Anti-IDOR.
 */
public class CitaService {

    private final CitaDAO citaDAO;
    private static final int MAX_CITAS_ACTIVAS_POR_CLIENTE = 2;

    public CitaService() {
        this.citaDAO = new CitaDAOImpl();
    }

    public CitaService(CitaDAO citaDAO) {
        this.citaDAO = citaDAO;
    }

    /**
     * Agenda una nueva cita validando limites y generando un Token Anti-IDOR.
     */
    public Cita agendarCita(Integer idCliente, Integer idMascota, Integer idServicio, Timestamp fechaHora,
                            BigDecimal valorTotal, BigDecimal descuento, String observaciones) throws Exception {

        if (idCliente == null || idMascota == null || idServicio == null || fechaHora == null) {
            throw new IllegalArgumentException("Cliente, mascota, servicio y fecha/hora son obligatorios.");
        }

        // Prevencion de citas fantasma (Maximo 2 citas pendientes o en proceso por cliente)
        int citasActivas = citaDAO.countActiveCitasByClienteId(idCliente);
        if (citasActivas >= MAX_CITAS_ACTIVAS_POR_CLIENTE) {
            throw new IllegalStateException(
                    "El cliente ya cuenta con " + citasActivas + " citas activas. Límite máximo permitido: " + MAX_CITAS_ACTIVAS_POR_CLIENTE
            );
        }

        // Generar Token Criptografico Impredecible Anti-IDOR (UUID v4)
        String tokenAcceso = SecurityUtils.generateAntiIdorToken();

        Cita nuevaCita = new Cita(idCliente, idMascota, idServicio, fechaHora, valorTotal, tokenAcceso);
        nuevaCita.setDescuentoAplicado(descuento != null ? descuento : BigDecimal.ZERO);
        nuevaCita.setObservacionesRecepcion(observaciones);
        nuevaCita.setEstado("PENDIENTE");

        return citaDAO.create(nuevaCita);
    }

    /**
     * Consulta segura del Album Digital de la Mascota mediante Token Impredecible (Anti-IDOR).
     */
    public Optional<Cita> consultarAlbumPorToken(String tokenAcceso) throws SQLException {
        if (tokenAcceso == null || tokenAcceso.trim().isEmpty()) {
            return Optional.empty();
        }
        return citaDAO.findByTokenAcceso(tokenAcceso.trim());
    }

    /**
     * Marca el servicio como COMPLETADO y registra observaciones finales de entrega.
     */
    public boolean completarServicio(Integer idCita, String observacionesEntrega) throws SQLException {
        Optional<Cita> optCita = citaDAO.findById(idCita);
        if (optCita.isPresent()) {
            Cita cita = optCita.get();
            cita.setEstado("COMPLETADA");
            cita.setObservacionesEntrega(observacionesEntrega);
            return citaDAO.update(cita);
        }
        return false;
    }

    /**
     * Asocia una fotografia (Antes o Despues) a la cita.
     */
    public boolean registrarFoto(Integer idCita, String tipo, String urlFoto, String publicId, Long bytes, String mime) throws SQLException {
        FotoMascota foto = new FotoMascota(idCita, tipo, urlFoto, publicId, bytes, mime);
        return citaDAO.addFoto(foto);
    }

    public List<Cita> listarTodas() throws SQLException {
        return citaDAO.findAll();
    }

    public List<Cita> listarPorEstado(String estado) throws SQLException {
        return citaDAO.findByEstado(estado);
    }

    public List<Servicio> obtenerServiciosDisponibles() throws SQLException {
        return citaDAO.findAllServicios();
    }
}
