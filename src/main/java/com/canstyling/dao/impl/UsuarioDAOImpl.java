package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.UsuarioDAO;
import com.canstyling.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC para UsuarioDAO.
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Usuario create(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (id_rol, nombres, apellidos, correo, password_hash, telefono, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, u.getIdRol());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getPasswordHash());
            ps.setString(6, u.getTelefono());
            ps.setString(7, u.getEstado() != null ? u.getEstado() : "ACTIVO");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setIdUsuario(rs.getInt(1));
                    }
                }
            }
        }
        return u;
    }

    @Override
    public Optional<Usuario> findById(Integer id) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                     "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.id_usuario = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> findAll() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                     "INNER JOIN roles r ON u.id_rol = r.id_rol ORDER BY u.id_usuario ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToUsuario(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET id_rol = ?, nombres = ?, apellidos = ?, correo = ?, " +
                     "password_hash = ?, telefono = ?, estado = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, u.getIdRol());
            ps.setString(2, u.getNombres());
            ps.setString(3, u.getApellidos());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getPasswordHash());
            ps.setString(6, u.getTelefono());
            ps.setString(7, u.getEstado());
            ps.setInt(8, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Usuario> findByCorreo(String correo) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                     "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.correo = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> findByTelefono(String telefono) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u " +
                     "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.telefono = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setIdRol(rs.getInt("id_rol"));
        u.setNombres(rs.getString("nombres"));
        u.setApellidos(rs.getString("apellidos"));
        u.setCorreo(rs.getString("correo"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setTelefono(rs.getString("telefono"));
        u.setEstado(rs.getString("estado"));
        u.setCreadoEn(rs.getTimestamp("creado_en"));
        u.setActualizadoEn(rs.getTimestamp("actualizado_en"));
        try {
            u.setNombreRol(rs.getString("nombre_rol"));
        } catch (SQLException ignored) {
        }
        return u;
    }
}
