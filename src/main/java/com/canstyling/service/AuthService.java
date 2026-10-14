package com.canstyling.service;

import com.canstyling.dao.ClienteDAO;
import com.canstyling.dao.UsuarioDAO;
import com.canstyling.dao.impl.ClienteDAOImpl;
import com.canstyling.dao.impl.UsuarioDAOImpl;
import com.canstyling.model.Cliente;
import com.canstyling.model.CodigoOTP;
import com.canstyling.model.Usuario;
import com.canstyling.util.SecurityUtils;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Servicio de Autenticacion y Control de Acceso.
 * Soporta autenticacion tradicional (correo y contraseña con BCrypt)
 * y autenticacion Passwordless mediante validacion de codigo OTP por correo electronico.
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO;
    private final ClienteDAO clienteDAO;
    private final OtpService otpService;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.clienteDAO = new ClienteDAOImpl();
        this.otpService = new OtpService();
    }

    public AuthService(UsuarioDAO usuarioDAO, ClienteDAO clienteDAO, OtpService otpService) {
        this.usuarioDAO = usuarioDAO;
        this.clienteDAO = clienteDAO;
        this.otpService = otpService;
    }

    /**
     * Autenticacion tradicional mediante correo electronico y contraseña.
     */
    public Optional<Usuario> loginConPassword(String correo, String passwordPlana) throws SQLException {
        if (correo == null || passwordPlana == null) {
            return Optional.empty();
        }
        Optional<Usuario> optUser = usuarioDAO.findByCorreo(correo.trim().toLowerCase());
        if (optUser.isPresent()) {
            Usuario u = optUser.get();
            if ("ACTIVO".equalsIgnoreCase(u.getEstado()) && SecurityUtils.verifyPassword(passwordPlana, u.getPasswordHash())) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    /**
     * Paso 1 de Login Passwordless: Solicita un codigo OTP enviado al correo del usuario/cliente.
     */
    public CodigoOTP solicitarOtpLogin(String correo) throws Exception {
        if (correo == null || !correo.contains("@")) {
            throw new IllegalArgumentException("Dirección de correo electrónico inválida.");
        }
        String correoNorm = correo.trim().toLowerCase();

        // Verificar si el correo pertenece a un usuario administrativo o a un cliente
        boolean existeUsuario = usuarioDAO.findByCorreo(correoNorm).isPresent();
        boolean existeCliente = clienteDAO.findByCorreo(correoNorm).isPresent();

        if (!existeUsuario && !existeCliente) {
            throw new IllegalArgumentException("No se encontró ninguna cuenta asociada al correo: " + correoNorm);
        }

        // Generar el codigo OTP con expiracion de 5 min y cooldown
        return otpService.generarOtp(correoNorm, "LOGIN");
    }

    /**
     * Paso 2 de Login Passwordless: Valida el codigo OTP ingresado y autentica la sesion.
     */
    public boolean verificarOtpLogin(String correo, String codigoOtpIngresado) throws SQLException {
        if (correo == null || codigoOtpIngresado == null) {
            return false;
        }
        String correoNorm = correo.trim().toLowerCase();
        return otpService.validarOtp(correoNorm, codigoOtpIngresado, "LOGIN");
    }

    /**
     * Registro de nuevo usuario administrativo con contraseña hasheada en BCrypt.
     */
    public Usuario registrarUsuario(Usuario usuario, String passwordPlana) throws Exception {
        if (usuarioDAO.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo ya se encuentra registrado.");
        }
        usuario.setPasswordHash(SecurityUtils.hashPassword(passwordPlana));
        return usuarioDAO.create(usuario);
    }
}
