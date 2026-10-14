package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.CitaDAO;
import com.canstyling.model.Cita;
import com.canstyling.model.FotoMascota;
import com.canstyling.model.Servicio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC para CitaDAO con soporte Anti-IDOR (Token UUID) y prevencion de citas fantasma.
 */
public class CitaDAOImpl implements CitaDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Cita create(Cita c) throws SQLException {
        String sql = "INSERT INTO citas (id_cliente, id_mascota, id_servicio, id_peluquero, fecha_hora, estado, " +
                     "valor_total, descuento_aplicado, token_acceso, observaciones_recepcion, observaciones_entrega) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getIdCliente());
            ps.setInt(2, c.getIdMascota());
            ps.setInt(3, c.getIdServicio());
            if (c.getIdPeluquero() != null) {
                ps.setInt(4, c.getIdPeluquero());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setTimestamp(5, c.getFechaHora());
            ps.setString(6, c.getEstado() != null ? c.getEstado() : "PENDIENTE");
            ps.setBigDecimal(7, c.getValorTotal());
            ps.setBigDecimal(8, c.getDescuentoAplicado());
            ps.setString(9, c.getTokenAcceso());
            ps.setString(10, c.getObservacionesRecepcion());
            ps.setString(11, c.getObservacionesEntrega());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setIdCita(rs.getInt(1));
                    }
                }
            }
        }
        return c;
    }

    @Override
    public Optional<Cita> findById(Integer id) throws SQLException {
        String sql = getBaseCitaSelect() + " WHERE c.id_cita = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cita c = mapResultSetToCita(rs);
                    c.setFotos(findFotosByCitaId(c.getIdCita()));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cita> findByTokenAcceso(String tokenAcceso) throws SQLException {
        String sql = getBaseCitaSelect() + " WHERE c.token_acceso = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, tokenAcceso);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cita c = mapResultSetToCita(rs);
                    c.setFotos(findFotosByCitaId(c.getIdCita()));
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Cita> findAll() throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = getBaseCitaSelect() + " ORDER BY c.fecha_hora DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapResultSetToCita(rs));
            }
        }
        return lista;
    }

    @Override
    public boolean update(Cita c) throws SQLException {
        String sql = "UPDATE citas SET id_mascota = ?, id_servicio = ?, id_peluquero = ?, fecha_hora = ?, " +
                     "estado = ?, valor_total = ?, descuento_aplicado = ?, observaciones_recepcion = ?, " +
                     "observaciones_entrega = ? WHERE id_cita = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, c.getIdMascota());
            ps.setInt(2, c.getIdServicio());
            if (c.getIdPeluquero() != null) {
                ps.setInt(3, c.getIdPeluquero());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setTimestamp(4, c.getFechaHora());
            ps.setString(5, c.getEstado());
            ps.setBigDecimal(6, c.getValorTotal());
            ps.setBigDecimal(7, c.getDescuentoAplicado());
            ps.setString(8, c.getObservacionesRecepcion());
            ps.setString(9, c.getObservacionesEntrega());
            ps.setInt(10, c.getIdCita());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM citas WHERE id_cita = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int countActiveCitasByClienteId(Integer idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM citas WHERE id_cliente = ? AND estado IN ('PENDIENTE', 'CONFIRMADA', 'EN_PROCESO')";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public List<Cita> findByEstado(String estado) throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = getBaseCitaSelect() + " WHERE c.estado = ? ORDER BY c.fecha_hora ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToCita(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Cita> findByClienteId(Integer idCliente) throws SQLException {
        List<Cita> lista = new ArrayList<>();
        String sql = getBaseCitaSelect() + " WHERE c.id_cliente = ? ORDER BY c.fecha_hora DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToCita(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public boolean addFoto(FotoMascota foto) throws SQLException {
        String sql = "INSERT INTO fotos_mascotas (id_cita, tipo, url_foto, cloudinary_public_id, tamano_bytes, mime_type) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, foto.getIdCita());
            ps.setString(2, foto.getTipo());
            ps.setString(3, foto.getUrlFoto());
            ps.setString(4, foto.getCloudinaryPublicId());
            if (foto.getTamanoBytes() != null) {
                ps.setLong(5, foto.getTamanoBytes());
            } else {
                ps.setNull(5, Types.BIGINT);
            }
            ps.setString(6, foto.getMimeType() != null ? foto.getMimeType() : "image/jpeg");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        foto.setIdFoto(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public List<FotoMascota> findFotosByCitaId(Integer idCita) throws SQLException {
        List<FotoMascota> fotos = new ArrayList<>();
        String sql = "SELECT * FROM fotos_mascotas WHERE id_cita = ? ORDER BY id_foto ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FotoMascota f = new FotoMascota();
                    f.setIdFoto(rs.getInt("id_foto"));
                    f.setIdCita(rs.getInt("id_cita"));
                    f.setTipo(rs.getString("tipo"));
                    f.setUrlFoto(rs.getString("url_foto"));
                    f.setCloudinaryPublicId(rs.getString("cloudinary_public_id"));
                    f.setTamanoBytes(rs.getLong("tamano_bytes"));
                    f.setMimeType(rs.getString("mime_type"));
                    f.setSubidoEn(rs.getTimestamp("subido_en"));
                    fotos.add(f);
                }
            }
        }
        return fotos;
    }

    @Override
    public List<Servicio> findAllServicios() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios WHERE estado = 'ACTIVO' ORDER BY id_servicio ASC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Servicio s = new Servicio();
                s.setIdServicio(rs.getInt("id_servicio"));
                s.setNombreServicio(rs.getString("nombre_servicio"));
                s.setDescripcion(rs.getString("descripcion"));
                s.setPrecioBase(rs.getBigDecimal("precio_base"));
                s.setDuracionMinutos(rs.getInt("duracion_minutos"));
                s.setEstado(rs.getString("estado"));
                servicios.add(s);
            }
        }
        return servicios;
    }

    private String getBaseCitaSelect() {
        return "SELECT c.*, CONCAT(cl.nombres, ' ', cl.apellidos) AS nombre_cliente, " +
               "m.nombre AS nombre_mascota, s.nombre_servicio, " +
               "CONCAT(u.nombres, ' ', u.apellidos) AS nombre_peluquero " +
               "FROM citas c " +
               "INNER JOIN clientes cl ON c.id_cliente = cl.id_cliente " +
               "INNER JOIN mascotas m ON c.id_mascota = m.id_mascota " +
               "INNER JOIN servicios s ON c.id_servicio = s.id_servicio " +
               "LEFT JOIN usuarios u ON c.id_peluquero = u.id_usuario";
    }

    private Cita mapResultSetToCita(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setIdMascota(rs.getInt("id_mascota"));
        c.setIdServicio(rs.getInt("id_servicio"));
        int idPeluquero = rs.getInt("id_peluquero");
        if (!rs.wasNull()) {
            c.setIdPeluquero(idPeluquero);
        }
        c.setFechaHora(rs.getTimestamp("fecha_hora"));
        c.setEstado(rs.getString("estado"));
        c.setValorTotal(rs.getBigDecimal("valor_total"));
        c.setDescuentoAplicado(rs.getBigDecimal("descuento_aplicado"));
        c.setTokenAcceso(rs.getString("token_acceso"));
        c.setObservacionesRecepcion(rs.getString("observaciones_recepcion"));
        c.setObservacionesEntrega(rs.getString("observaciones_entrega"));
        c.setCreadoEn(rs.getTimestamp("creado_en"));
        c.setActualizadoEn(rs.getTimestamp("actualizado_en"));

        try {
            c.setNombreCliente(rs.getString("nombre_cliente"));
            c.setNombreMascota(rs.getString("nombre_mascota"));
            c.setNombreServicio(rs.getString("nombre_servicio"));
            c.setNombrePeluquero(rs.getString("nombre_peluquero"));
        } catch (SQLException ignored) {
        }

        return c;
    }
}
