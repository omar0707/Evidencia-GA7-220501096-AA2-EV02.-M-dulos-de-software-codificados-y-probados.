package com.canstyling.dao;

import com.canstyling.model.Cita;
import com.canstyling.model.FotoMascota;
import com.canstyling.model.Servicio;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para Citas, Turnos, Albumes y Servicios.
 */
public interface CitaDAO extends GenericDAO<Cita, Integer> {

    /**
     * Busca una cita utilizando su token de acceso Anti-IDOR (UUID).
     */
    Optional<Cita> findByTokenAcceso(String tokenAcceso) throws SQLException;

    /**
     * Cuenta cuantas citas activas/pendientes tiene un cliente (Previene citas fantasma).
     */
    int countActiveCitasByClienteId(Integer idCliente) throws SQLException;

    /**
     * Lista citas por estado (PENDIENTE, CONFIRMADA, EN_PROCESO, COMPLETADA).
     */
    List<Cita> findByEstado(String estado) throws SQLException;

    /**
     * Lista citas de un cliente en especifico.
     */
    List<Cita> findByClienteId(Integer idCliente) throws SQLException;

    /**
     * Agrega una foto (Antes/Despues) a la cita.
     */
    boolean addFoto(FotoMascota foto) throws SQLException;

    /**
     * Obtiene las fotos de una cita.
     */
    List<FotoMascota> findFotosByCitaId(Integer idCita) throws SQLException;

    /**
     * Obtiene todos los servicios activos disponibles.
     */
    List<Servicio> findAllServicios() throws SQLException;
}
