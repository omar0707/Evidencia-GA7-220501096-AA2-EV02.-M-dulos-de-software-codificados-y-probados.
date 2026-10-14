package com.canstyling.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz generica para operaciones CRUD (Create, Read, Update, Delete) en base de datos.
 *
 * @param <T> Tipo de entidad modelo
 * @param <K> Tipo de la clave primaria (ej. Integer)
 */
public interface GenericDAO<T, K> {

    /**
     * Inserta un nuevo registro en la base de datos (CREATE).
     */
    T create(T entity) throws SQLException;

    /**
     * Busca un registro por su clave primaria (READ).
     */
    Optional<T> findById(K id) throws SQLException;

    /**
     * Obtiene todos los registros disponibles (READ ALL).
     */
    List<T> findAll() throws SQLException;

    /**
     * Actualiza un registro existente (UPDATE).
     */
    boolean update(T entity) throws SQLException;

    /**
     * Elimina un registro por su identificador (DELETE).
     */
    boolean deleteById(K id) throws SQLException;
}
