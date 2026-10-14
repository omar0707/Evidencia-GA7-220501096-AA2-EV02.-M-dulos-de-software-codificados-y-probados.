package com.canstyling.dao;

import com.canstyling.model.Mascota;
import com.canstyling.model.Raza;
import java.sql.SQLException;
import java.util.List;

/**
 * Contrato de acceso a datos para las Mascotas y Razas Caninas.
 */
public interface MascotaDAO extends GenericDAO<Mascota, Integer> {

    /**
     * Obtiene todas las mascotas pertenecientes a un cliente.
     */
    List<Mascota> findByClienteId(Integer idCliente) throws SQLException;

    /**
     * Obtiene el catalogo de razas caninas registradas.
     */
    List<Raza> findAllRazas() throws SQLException;
}
