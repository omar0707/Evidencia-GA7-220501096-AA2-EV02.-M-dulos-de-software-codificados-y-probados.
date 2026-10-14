package com.canstyling.service;

import com.canstyling.config.EnvConfig;
import com.canstyling.dao.CodigoOtpDAO;
import com.canstyling.dao.impl.CodigoOtpDAOImpl;
import com.canstyling.model.CodigoOTP;
import com.canstyling.util.SecurityUtils;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/**
 * Servicio de Generacion y Validacion de Codigos OTP de Seguridad.
 * Reglas de seguridad implementadas:
 * 1. Generacion con SecureRandom (6 digitos numericos).
 * 2. Tiempo de vida maximo de 5 minutos.
 * 3. Limite de 3 intentos fallidos antes de invalidar el codigo.
 * 4. Consumo de un solo uso (usado = true).
 * 5. Cooldown / Rate Limit de 60 segundos entre solicitudes hacia el mismo destino.
 */
public class OtpService {

    private final CodigoOtpDAO otpDAO;
    private final int expirationMinutes;
    private final int maxAttempts;
    private final int cooldownSeconds;

    public OtpService() {
        this.otpDAO = new CodigoOtpDAOImpl();
        this.expirationMinutes = EnvConfig.getInt("OTP_EXPIRATION_MINUTES", 5);
        this.maxAttempts = EnvConfig.getInt("OTP_MAX_ATTEMPTS", 3);
        this.cooldownSeconds = EnvConfig.getInt("OTP_COOLDOWN_SECONDS", 60);
    }

    public OtpService(CodigoOtpDAO otpDAO) {
        this.otpDAO = otpDAO;
        this.expirationMinutes = 5;
        this.maxAttempts = 3;
        this.cooldownSeconds = 60;
    }

    /**
     * Genera un nuevo codigo OTP seguro para el destino indicado (correo o WhatsApp/telefono).
     *
     * @param destino Direccion de correo o numero telefonico.
     * @param proposito Proposito del OTP (LOGIN, REGISTRO, etc.)
     * @return El objeto CodigoOTP con el codigo en texto plano disponible temporalmente en memoria.
     */
    public CodigoOTP generarOtp(String destino, String proposito) throws Exception {
        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("El destino (correo o teléfono) es requerido.");
        }
        String destinoNormalizado = destino.trim().toLowerCase();

        // 1. Validar Cooldown (Rate Limiting)
        if (otpDAO.hasRecentOtpWithinCooldown(destinoNormalizado, cooldownSeconds)) {
            throw new IllegalStateException("Debe esperar " + cooldownSeconds + " segundos antes de solicitar otro código.");
        }

        // 2. Invalidar OTPs previos activos para ese destino
        otpDAO.invalidatePreviousOtps(destinoNormalizado, proposito);

        // 3. Generar codigo criptografico de 6 digitos
        String codigoPlano = SecurityUtils.generateOtpCode(6);
        String codigoHash = SecurityUtils.hashSha256(codigoPlano);

        // 4. Calcular tiempo de expiracion (5 minutos)
        Timestamp expiraEn = Timestamp.from(Instant.now().plusSeconds(expirationMinutes * 60L));

        CodigoOTP nuevoOtp = new CodigoOTP(destinoNormalizado, codigoHash, proposito, expiraEn);
        nuevoOtp.setCodigoPlano(codigoPlano);

        otpDAO.save(nuevoOtp);
        return nuevoOtp;
    }

    /**
     * Valida un codigo OTP ingresado por el usuario.
     *
     * @param destino Correo o telefono
     * @param codigoIngresado Codigo de 6 digitos ingresado
     * @param proposito Proposito esperado
     * @return true si el codigo es valido y fue consumido con exito, false en caso contrario.
     */
    public boolean validarOtp(String destino, String codigoIngresado, String proposito) throws SQLException {
        if (destino == null || codigoIngresado == null) {
            return false;
        }
        String destinoNormalizado = destino.trim().toLowerCase();

        Optional<CodigoOTP> optOtp = otpDAO.findLatestActiveByDestino(destinoNormalizado, proposito);
        if (optOtp.isEmpty()) {
            return false; // No hay OTP activo o ya expiro
        }

        CodigoOTP otp = optOtp.get();

        // Verificar si supero el limite de intentos fallidos
        if (otp.getIntentosFallidos() >= maxAttempts) {
            otpDAO.markAsUsed(otp.getIdOtp()); // Invalidar por seguridad
            return false;
        }

        // Hashear el codigo ingresado para comparar
        String hashIngresado = SecurityUtils.hashSha256(codigoIngresado.trim());

        if (otp.getCodigoHash().equals(hashIngresado)) {
            // Codigo correcto: marcar como usado (consumo unico)
            otpDAO.markAsUsed(otp.getIdOtp());
            return true;
        } else {
            // Codigo incorrecto: incrementar intentos fallidos
            otpDAO.incrementIntentosFallidos(otp.getIdOtp());
            if (otp.getIntentosFallidos() + 1 >= maxAttempts) {
                otpDAO.markAsUsed(otp.getIdOtp()); // Bloquear inmediatamente
            }
            return false;
        }
    }
}
