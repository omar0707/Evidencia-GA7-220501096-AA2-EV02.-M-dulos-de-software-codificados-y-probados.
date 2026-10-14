package com.canstyling.dao;

import com.canstyling.model.Usuario;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Contrato de acceso a datos para Usuarios y Roles del sistema.
 */
public interface UsuarioDAO extends GenericDAO<Usuario, Integer> {

    /**
     * Busca un usuario por su correo electronico.
     */
    Optional<Usuario> findByCorreo(String correo) throws SQLException;

    /**
     * Busca un usuario por su numero telefonico.
     */
    Optional<Usuario> findByTelefono(String telefono) throws SQLException;
}
