package com.canstyling;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.model.Cliente;
import com.canstyling.model.CodigoOTP;
import com.canstyling.service.AuthService;
import com.canstyling.service.CitaService;
import com.canstyling.service.ClienteService;
import com.canstyling.service.OtpService;
import com.canstyling.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de Pruebas Automatizadas para la Evidencia GA7-220501096-AA2-EV01.
 * Valida los componentes de seguridad, generacion de tokens, OTP y operaciones CRUD.
 */
public class ModuloSoftwareTest {

    @Test
    @DisplayName("Test 1: Generación y formato de Token Anti-IDOR (UUID v4)")
    void testAntiIdorTokenGeneration() {
        String token = SecurityUtils.generateAntiIdorToken();
        assertNotNull(token, "El token Anti-IDOR no debe ser nulo");
        assertEquals(36, token.length(), "El token UUID v4 debe tener 36 caracteres");
        assertTrue(token.contains("-"), "El token UUID debe contener guiones separadores");
    }

    @Test
    @DisplayName("Test 2: Generación y Hashing de Códigos OTP con SecureRandom")
    void testOtpGenerationAndHashing() {
        String otpCode = SecurityUtils.generateOtpCode(6);
        assertNotNull(otpCode);
        assertEquals(6, otpCode.length(), "El código OTP debe ser de 6 dígitos numéricos");
        assertTrue(otpCode.matches("\\d{6}"), "El código OTP debe contener únicamente dígitos");

        String hash1 = SecurityUtils.hashSha256(otpCode);
        String hash2 = SecurityUtils.hashSha256(otpCode);
        assertEquals(hash1, hash2, "El hash SHA-256 debe ser determinista para el mismo código");
    }

    @Test
    @DisplayName("Test 3: Cifrado de Contraseñas con BCrypt y Verificación")
    void testPasswordBCrypt() {
        String rawPass = "SenaPassword2026*";
        String hashed = SecurityUtils.hashPassword(rawPass);

        assertNotNull(hashed);
        assertNotEquals(rawPass, hashed, "La contraseña no debe almacenarse en texto plano");

        boolean matches = SecurityUtils.verifyPassword(rawPass, hashed);
        assertTrue(matches, "La verificación con la contraseña correcta debe ser exitosa");

        boolean wrongPass = SecurityUtils.verifyPassword("ClaveInvalida", hashed);
        assertFalse(wrongPass, "La verificación con contraseña incorrecta debe fallar");
    }

    @Test
    @DisplayName("Test 4: Flujo CRUD de Clientes (Si la BD MySQL está activa)")
    void testClienteCrudOperations() {
        if (!DatabaseConnection.getInstance().testConnection()) {
            System.out.println("[INFO] Saltando prueba CRUD directa en base de datos (MySQL no activo en localhost:3306).");
            return;
        }

        ClienteService clienteService = new ClienteService();
        String docUnico = "TEST_" + System.currentTimeMillis() % 100000;
        String emailUnico = "test_" + System.currentTimeMillis() % 100000 + "@canstyling.com";

        Cliente nuevo = new Cliente("CC", docUnico, "Pedro", "Gómez", emailUnico, "Calle 10 # 5-20", "Montería");

        try {
            // 1. Create
            Cliente creado = clienteService.registrarCliente(nuevo, "3001239988", "WHATSAPP");
            assertNotNull(creado.getIdCliente(), "El cliente registrado debe tener un ID asignado");

            // 2. Read
            Optional<Cliente> consultado = clienteService.buscarPorId(creado.getIdCliente());
            assertTrue(consultado.isPresent(), "El cliente debe existir en la base de datos");
            assertEquals("Pedro", consultado.get().getNombres());

            // 3. Update
            creado.setNombres("Pedro Antonio");
            boolean updated = clienteService.actualizarCliente(creado);
            assertTrue(updated, "La actualización del cliente debe ser exitosa");

            // 4. Delete
            boolean deleted = clienteService.eliminarCliente(creado.getIdCliente());
            assertTrue(deleted, "La eliminación del cliente de prueba debe ser exitosa");
        } catch (Exception e) {
            fail("Error en el flujo CRUD de cliente: " + e.getMessage());
        }
    }
}
