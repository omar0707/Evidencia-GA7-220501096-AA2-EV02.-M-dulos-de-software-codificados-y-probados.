package com.canstyling.dao;

import com.canstyling.model.Cliente;
import com.canstyling.model.ClienteTelefono;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para la entidad Cliente y sus telefonos asociados.
 */
public interface ClienteDAO extends GenericDAO<Cliente, Integer> {

    /**
     * Busca un cliente por numero de documento de identidad.
     */
    Optional<Cliente> findByDocumento(String numeroDocumento) throws SQLException;

    /**
     * Busca un cliente por su correo electronico.
     */
    Optional<Cliente> findByCorreo(String correo) throws SQLException;

    /**
     * Busca un cliente a partir de un numero de telefono o WhatsApp.
     */
    Optional<Cliente> findByTelefono(String numeroTelefono) throws SQLException;

    /**
     * Agrega un nuevo numero telefonico al cliente.
     */
    boolean addTelefono(ClienteTelefono telefono) throws SQLException;

    /**
     * Obtiene la lista de telefonos de un cliente.
     */
    List<ClienteTelefono> findTelefonosByClienteId(Integer idCliente) throws SQLException;

    /**
     * Elimina un telefono especifico.
     */
    boolean deleteTelefono(Integer idTelefono) throws SQLException;
}
