package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.MascotaDAO;
import com.canstyling.model.Mascota;
import com.canstyling.model.Raza;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC para MascotaDAO con consultas preparadas seguras.
 */
public class MascotaDAOImpl implements MascotaDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Mascota create(Mascota m) throws SQLException {
        String sql = "INSERT INTO mascotas (id_cliente, id_raza, nombre, genero, edad_anos, peso_kg, color, alergias_condiciones, agresivo, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getIdCliente());
            ps.setInt(2, m.getIdRaza());
            ps.setString(3, m.getNombre());
            ps.setString(4, m.getGenero());
            ps.setInt(5, m.getEdadAnos() != null ? m.getEdadAnos() : 0);
            if (m.getPesoKg() != null) {
                ps.setBigDecimal(6, m.getPesoKg());
            } else {
                ps.setNull(6, Types.DECIMAL);
            }
            ps.setString(7, m.getColor());
            ps.setString(8, m.getAlergiasCondiciones());
            ps.setBoolean(9, m.isAgresivo());
            ps.setString(10, m.getEstado() != null ? m.getEstado() : "ACTIVO");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        m.setIdMascota(rs.getInt(1));
                    }
                }
            }
        }
        return m;
    }

    @Override
    public Optional<Mascota> findById(Integer id) throws SQLException {
        String sql = "SELECT m.*, CONCAT(c.nombres, ' ', c.apellidos) AS nombre_cliente, r.nombre_raza " +
                     "FROM mascotas m " +
                     "INNER JOIN clientes c ON m.id_cliente = c.id_cliente " +
                     "INNER JOIN razas r ON m.id_raza = r.id_raza " +
                     "WHERE m.id_mascota = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMascota(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Mascota> findAll() throws SQLException {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT m.*, CONCAT(c.nombres, ' ', c.apellidos) AS nombre_cliente, r.nombre_raza " +
                     "FROM mascotas m " +
                     "INNER JOIN clientes c ON m.id_cliente = c.id_cliente " +
                     "INNER JOIN razas r ON m.id_raza = r.id_raza " +
                     "ORDER BY m.id_mascota DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToMascota(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Mascota m) throws SQLException {
        String sql = "UPDATE mascotas SET id_raza = ?, nombre = ?, genero = ?, edad_anos = ?, " +
                     "peso_kg = ?, color = ?, alergias_condiciones = ?, agresivo = ?, estado = ? " +
                     "WHERE id_mascota = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, m.getIdRaza());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getGenero());
            ps.setInt(4, m.getEdadAnos());
            ps.setBigDecimal(5, m.getPesoKg());
            ps.setString(6, m.getColor());
            ps.setString(7, m.getAlergiasCondiciones());
            ps.setBoolean(8, m.isAgresivo());
            ps.setString(9, m.getEstado());
            ps.setInt(10, m.getIdMascota());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM mascotas WHERE id_mascota = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Mascota> findByClienteId(Integer idCliente) throws SQLException {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT m.*, CONCAT(c.nombres, ' ', c.apellidos) AS nombre_cliente, r.nombre_raza " +
                     "FROM mascotas m " +
                     "INNER JOIN clientes c ON m.id_cliente = c.id_cliente " +
                     "INNER JOIN razas r ON m.id_raza = r.id_raza " +
                     "WHERE m.id_cliente = ? ORDER BY m.nombre ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToMascota(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Raza> findAllRazas() throws SQLException {
        List<Raza> razas = new ArrayList<>();
        String sql = "SELECT * FROM razas ORDER BY nombre_raza ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Raza r = new Raza();
                r.setIdRaza(rs.getInt("id_raza"));
                r.setNombreRaza(rs.getString("nombre_raza"));
                r.setTamano(rs.getString("tamano"));
                r.setTipoPelo(rs.getString("tipo_pelo"));
                r.setDescripcion(rs.getString("descripcion"));
                razas.add(r);
            }
        }
        return razas;
    }

    private Mascota mapResultSetToMascota(ResultSet rs) throws SQLException {
        Mascota m = new Mascota();
        m.setIdMascota(rs.getInt("id_mascota"));
        m.setIdCliente(rs.getInt("id_cliente"));
        m.setIdRaza(rs.getInt("id_raza"));
        m.setNombre(rs.getString("nombre"));
        m.setGenero(rs.getString("genero"));
        m.setEdadAnos(rs.getInt("edad_anos"));
        m.setPesoKg(rs.getBigDecimal("peso_kg"));
        m.setColor(rs.getString("color"));
        m.setAlergiasCondiciones(rs.getString("alergias_condiciones"));
        m.setAgresivo(rs.getBoolean("agresivo"));
        m.setEstado(rs.getString("estado"));
        m.setCreadoEn(rs.getTimestamp("creado_en"));
        m.setActualizadoEn(rs.getTimestamp("actualizado_en"));

        try {
            m.setNombreCliente(rs.getString("nombre_cliente"));
            m.setNombreRaza(rs.getString("nombre_raza"));
        } catch (SQLException ignored) {
        }
        return m;
    }
}
