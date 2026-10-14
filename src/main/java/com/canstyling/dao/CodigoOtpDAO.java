package com.canstyling.dao;

import com.canstyling.model.CodigoOTP;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Contrato de acceso a datos para Codigos OTP de autenticacion segura.
 */
public interface CodigoOtpDAO {

    /**
     * Guarda un nuevo codigo OTP generado.
     */
    CodigoOTP save(CodigoOTP otp) throws SQLException;

    /**
     * Busca el ultimo codigo OTP activo (no usado y no expirado) para un destino (correo/telefono).
     */
    Optional<CodigoOTP> findLatestActiveByDestino(String destino, String tipoProposito) throws SQLException;

    /**
     * Incrementa el contador de intentos fallidos de un codigo OTP.
     */
    void incrementIntentosFallidos(Integer idOtp) throws SQLException;

    /**
     * Marca un codigo OTP como consumido/usado.
     */
    void markAsUsed(Integer idOtp) throws SQLException;

    /**
     * Invalida todos los codigos previos para un destino antes de generar uno nuevo.
     */
    void invalidatePreviousOtps(String destino, String tipoProposito) throws SQLException;

    /**
     * Verifica si existe un codigo reciente dentro del periodo de cooldown (ej. 60 segundos).
     */
    boolean hasRecentOtpWithinCooldown(String destino, int cooldownSeconds) throws SQLException;
}
