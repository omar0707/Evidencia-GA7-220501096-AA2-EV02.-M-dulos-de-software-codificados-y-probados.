package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.ClienteDAO;
import com.canstyling.model.Cliente;
import com.canstyling.model.ClienteTelefono;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC para ClienteDAO utilizando PreparedStatements parametrizados (Anti-SQLi).
 */
public class ClienteDAOImpl implements ClienteDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Cliente create(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (id_usuario, tipo_documento, numero_documento, nombres, apellidos, correo, direccion, ciudad, estado, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (cliente.getIdUsuario() != null) {
                ps.setInt(1, cliente.getIdUsuario());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, cliente.getTipoDocumento());
            ps.setString(3, cliente.getNumeroDocumento());
            ps.setString(4, cliente.getNombres());
            ps.setString(5, cliente.getApellidos());
            ps.setString(6, cliente.getCorreo());
            ps.setString(7, cliente.getDireccion());
            ps.setString(8, cliente.getCiudad() != null ? cliente.getCiudad() : "Montería");
            ps.setString(9, cliente.getEstado() != null ? cliente.getEstado() : "ACTIVO");
            ps.setString(10, cliente.getObservaciones());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        cliente.setIdCliente(rs.getInt(1));
                    }
                }
            }
        }

        // Guardar telefonos asociados si existen
        if (cliente.getIdCliente() != null && cliente.getTelefonos() != null) {
            for (ClienteTelefono tel : cliente.getTelefonos()) {
                tel.setIdCliente(cliente.getIdCliente());
                addTelefono(tel);
            }
        }

        return cliente;
    }

    @Override
    public Optional<Cliente> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapResultSetToCliente(rs);
                    c.setTelefonos(findTelefonosByClienteId(c.getIdCliente()));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> findAll() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY id_cliente DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cliente c = mapResultSetToCliente(rs);
                c.setTelefonos(findTelefonosByClienteId(c.getIdCliente()));
                lista.add(c);
            }
        }
        return lista;
    }

    @Override
    public boolean update(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET tipo_documento = ?, numero_documento = ?, nombres = ?, apellidos = ?, " +
                     "correo = ?, direccion = ?, ciudad = ?, estado = ?, observaciones = ? WHERE id_cliente = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, cliente.getTipoDocumento());
            ps.setString(2, cliente.getNumeroDocumento());
            ps.setString(3, cliente.getNombres());
            ps.setString(4, cliente.getApellidos());
            ps.setString(5, cliente.getCorreo());
            ps.setString(6, cliente.getDireccion());
            ps.setString(7, cliente.getCiudad());
            ps.setString(8, cliente.getEstado());
            ps.setString(9, cliente.getObservaciones());
            ps.setInt(10, cliente.getIdCliente());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Cliente> findByDocumento(String numeroDocumento) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE numero_documento = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, numeroDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapResultSetToCliente(rs);
                    c.setTelefonos(findTelefonosByClienteId(c.getIdCliente()));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> findByCorreo(String correo) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE correo = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapResultSetToCliente(rs);
                    c.setTelefonos(findTelefonosByClienteId(c.getIdCliente()));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> findByTelefono(String numeroTelefono) throws SQLException {
        String sql = "SELECT c.* FROM clientes c " +
                     "INNER JOIN cliente_telefonos ct ON c.id_cliente = ct.id_cliente " +
                     "WHERE ct.numero_telefono = ? LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, numeroTelefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = mapResultSetToCliente(rs);
                    c.setTelefonos(findTelefonosByClienteId(c.getIdCliente()));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean addTelefono(ClienteTelefono telefono) throws SQLException {
        String sql = "INSERT INTO cliente_telefonos (id_cliente, numero_telefono, tipo, es_principal, verificado) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE tipo = VALUES(tipo), es_principal = VALUES(es_principal)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, telefono.getIdCliente());
            ps.setString(2, telefono.getNumeroTelefono());
            ps.setString(3, telefono.getTipo());
            ps.setBoolean(4, telefono.isEsPrincipal());
            ps.setBoolean(5, telefono.isVerificado());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        telefono.setIdTelefono(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<ClienteTelefono> findTelefonosByClienteId(Integer idCliente) throws SQLException {
        List<ClienteTelefono> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM cliente_telefonos WHERE id_cliente = ? ORDER BY es_principal DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ClienteTelefono tel = new ClienteTelefono();
                    tel.setIdTelefono(rs.getInt("id_telefono"));
                    tel.setIdCliente(rs.getInt("id_cliente"));
                    tel.setNumeroTelefono(rs.getString("numero_telefono"));
                    tel.setTipo(rs.getString("tipo"));
                    tel.setEsPrincipal(rs.getBoolean("es_principal"));
                    tel.setVerificado(rs.getBoolean("verificado"));
                    tel.setCreadoEn(rs.getTimestamp("creado_en"));
                    telefonos.add(tel);
                }
            }
        }
        return telefonos;
    }

    @Override
    public boolean deleteTelefono(Integer idTelefono) throws SQLException {
        String sql = "DELETE FROM cliente_telefonos WHERE id_telefono = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idTelefono);
            return ps.executeUpdate() > 0;
        }
    }

    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        int idUser = rs.getInt("id_usuario");
        if (!rs.wasNull()) {
            c.setIdUsuario(idUser);
        }
        c.setTipoDocumento(rs.getString("tipo_documento"));
        c.setNumeroDocumento(rs.getString("numero_documento"));
        c.setNombres(rs.getString("nombres"));
        c.setApellidos(rs.getString("apellidos"));
        c.setCorreo(rs.getString("correo"));
        c.setDireccion(rs.getString("direccion"));
        c.setCiudad(rs.getString("ciudad"));
        c.setEstado(rs.getString("estado"));
        c.setObservaciones(rs.getString("observaciones"));
        c.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        c.setActualizadoEn(rs.getTimestamp("actualizado_en"));
        return c;
    }
}
