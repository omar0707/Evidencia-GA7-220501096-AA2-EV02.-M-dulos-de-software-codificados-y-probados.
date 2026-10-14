package com.canstyling.dao.impl;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.dao.CodigoOtpDAO;
import com.canstyling.model.CodigoOTP;

import java.sql.*;
import java.util.Optional;

/**
 * Implementacion JDBC para CodigoOtpDAO que gestiona almacenamiento de OTPs,
 * limites de intentos, expiracion de 5 minutos y control de cooldown.
 */
public class CodigoOtpDAOImpl implements CodigoOtpDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public CodigoOTP save(CodigoOTP otp) throws SQLException {
        String sql = "INSERT INTO codigos_otp (destino, codigo_hash, tipo_proposito, intentos_fallidos, usado, expira_en) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, otp.getDestino());
            ps.setString(2, otp.getCodigoHash());
            ps.setString(3, otp.getTipoProposito());
            ps.setInt(4, otp.getIntentosFallidos());
            ps.setBoolean(5, otp.isUsado());
            ps.setTimestamp(6, otp.getExpiraEn());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        otp.setIdOtp(rs.getInt(1));
                    }
                }
            }
        }
        return otp;
    }

    @Override
    public Optional<CodigoOTP> findLatestActiveByDestino(String destino, String tipoProposito) throws SQLException {
        String sql = "SELECT * FROM codigos_otp " +
                     "WHERE destino = ? AND tipo_proposito = ? AND usado = FALSE AND expira_en > NOW() " +
                     "ORDER BY id_otp DESC LIMIT 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, destino);
            ps.setString(2, tipoProposito);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CodigoOTP otp = new CodigoOTP();
                    otp.setIdOtp(rs.getInt("id_otp"));
                    otp.setDestino(rs.getString("destino"));
                    otp.setCodigoHash(rs.getString("codigo_hash"));
                    otp.setTipoProposito(rs.getString("tipo_proposito"));
                    otp.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                    otp.setUsado(rs.getBoolean("usado"));
                    otp.setExpiraEn(rs.getTimestamp("expira_en"));
                    otp.setCreadoEn(rs.getTimestamp("creado_en"));
                    return Optional.of(otp);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void incrementIntentosFallidos(Integer idOtp) throws SQLException {
        String sql = "UPDATE codigos_otp SET intentos_fallidos = intentos_fallidos + 1 WHERE id_otp = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idOtp);
            ps.executeUpdate();
        }
    }

    @Override
    public void markAsUsed(Integer idOtp) throws SQLException {
        String sql = "UPDATE codigos_otp SET usado = TRUE WHERE id_otp = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, idOtp);
            ps.executeUpdate();
        }
    }

    @Override
    public void invalidatePreviousOtps(String destino, String tipoProposito) throws SQLException {
        String sql = "UPDATE codigos_otp SET usado = TRUE WHERE destino = ? AND tipo_proposito = ? AND usado = FALSE";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, destino);
            ps.setString(2, tipoProposito);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean hasRecentOtpWithinCooldown(String destino, int cooldownSeconds) throws SQLException {
        String sql = "SELECT COUNT(*) FROM codigos_otp " +
                     "WHERE destino = ? AND creado_en >= (NOW() - INTERVAL ? SECOND)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, destino);
            ps.setInt(2, cooldownSeconds);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
