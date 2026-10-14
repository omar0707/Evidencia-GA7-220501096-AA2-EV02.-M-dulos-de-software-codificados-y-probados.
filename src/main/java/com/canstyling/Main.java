package com.canstyling;

import com.canstyling.config.DatabaseConnection;
import com.canstyling.config.EnvConfig;
import com.canstyling.model.Cita;
import com.canstyling.model.Cliente;
import com.canstyling.model.CodigoOTP;
import com.canstyling.model.FotoMascota;
import com.canstyling.model.Mascota;
import com.canstyling.service.AuthService;
import com.canstyling.service.CitaService;
import com.canstyling.service.ClienteService;
import com.canstyling.service.OtpService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Aplicacion de Demostracion y Ejecucion de Modulos - CanStyling.
 * Guia de Aprendizaje 07 - Evidencia GA7-220501096-AA2-EV01.
 *
 * @author Omar Ulpiano Molina Malaver
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ClienteService clienteService = new ClienteService();
    private static final CitaService citaService = new CitaService();
    private static final OtpService otpService = new OtpService();
    private static final AuthService authService = new AuthService();

    public static void main(String[] args) {
        imprimirBanner();

        boolean salir = false;
        while (!salir) {
            mostrarMenuPrincipal();
            System.out.print("➤ Seleccione una opción [1-8]: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String opcion = scanner.nextLine().trim();

            try {
                switch (opcion) {
                    case "1":
                        probarConexionBaseDatos();
                        break;
                    case "2":
                        moduloGestionClientes();
                        break;
                    case "3":
                        moduloGestionMascotas();
                        break;
                    case "4":
                        moduloGestionCitas();
                        break;
                    case "5":
                        moduloConsultarAlbumAntiIdor();
                        break;
                    case "6":
                        moduloLoginPorCorreoConOtp();
                        break;
                    case "7":
                        ejecutarDemoIntegralAutomatica();
                        break;
                    case "8":
                        salir = true;
                        System.out.println("\n[✔] Gracias por utilizar el sistema CanStyling. ¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("\n[!] Opción no válida. Por favor intente nuevamente.");
                }
            } catch (Exception e) {
                System.out.println("\n[✘] Error durante la operación: " + e.getMessage());
            }

            if (!salir) {
                System.out.println("\nPresione ENTER para continuar...");
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                } else {
                    break;
                }
            }
        }

        DatabaseConnection.getInstance().closeConnection();
    }

    private static void imprimirBanner() {
        System.out.println("================================================================================");
        System.out.println("   CANSTYLING - SISTEMA DE GESTIÓN INTEGRAL PARA PELUQUERÍA CANINA 🐾       ");
        System.out.println("   SENA ADSO | Evidencia: GA7-220501096-AA2-EV01 - Módulos Java & JDBC        ");
        System.out.println("   Aprendiz: Omar Ulpiano Molina Malaver | Instructor: Adonay Sánchez Vertel ");
        System.out.println("================================================================================");
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n=========================== MENÚ PRINCIPAL ===========================");
        System.out.println(" 1. Probar Conexión a Base de Datos MySQL (JDBC)");
        System.out.println(" 2. Gestión de Clientes (CRUD y Soporte Multi-Teléfono)");
        System.out.println(" 3. Gestión de Mascotas y Fichas Clínicas");
        System.out.println(" 4. Gestión de Citas y Turnos (Seguridad y Anti-Citas Fantasma)");
        System.out.println(" 5. Consultar Álbum Digital de Mascota mediante Token Anti-IDOR");
        System.out.println(" 6. Autenticación / Login por Correo Electrónico con Validación OTP");
        System.out.println(" 7. Ejecutar Demostración Integral Automatizada (Test de Componentes)");
        System.out.println(" 8. Salir");
        System.out.println("======================================================================");
    }

    private static void probarConexionBaseDatos() {
        System.out.println("\n--- [1] TEST DE CONEXIÓN A BASE DE DATOS ---");
        System.out.println("Configuración activa:");
        System.out.println(" • Host: " + EnvConfig.get("DB_HOST", "localhost"));
        System.out.println(" • Puerto: " + EnvConfig.get("DB_PORT", "3306"));
        System.out.println(" • Base de datos: " + EnvConfig.get("DB_NAME", "peluqueria_canina_db"));
        System.out.println(" • Usuario: " + EnvConfig.get("DB_USER", "root"));

        boolean ok = DatabaseConnection.getInstance().testConnection();
        if (ok) {
            System.out.println("[✔] Conexión JDBC establecida con éxito.");
        } else {
            System.out.println("[!] No se pudo conectar a MySQL local.");
            System.out.println("    Asegúrate de tener XAMPP/MySQL encendido e importar los scripts de la carpeta database/.");
        }
    }

    private static void moduloGestionClientes() throws Exception {
        System.out.println("\n--- [2] GESTIÓN DE CLIENTES ---");
        System.out.println("a. Listar todos los clientes");
        System.out.println("b. Registrar nuevo cliente");
        System.out.println("c. Buscar cliente por documento");
        System.out.print("Seleccione sub-opción [a-c]: ");
        String sub = scanner.nextLine().trim().toLowerCase();

        switch (sub) {
            case "a":
                List<Cliente> clientes = clienteService.listarTodos();
                if (clientes.isEmpty()) {
                    System.out.println("No hay clientes registrados en la base de datos.");
                } else {
                    System.out.printf("%-5s | %-12s | %-25s | %-25s | %-15s\n", "ID", "Documento", "Nombre Completo", "Correo", "Tel. Principal");
                    System.out.println("-------------------------------------------------------------------------------------------------");
                    for (Cliente c : clientes) {
                        System.out.printf("%-5d | %-12s | %-25s | %-25s | %-15s\n",
                                c.getIdCliente(),
                                c.getTipoDocumento() + " " + c.getNumeroDocumento(),
                                c.getNombreCompleto(),
                                c.getCorreo(),
                                c.getTelefonoPrincipal());
                    }
                }
                break;
            case "b":
                System.out.print("Tipo Documento (CC/CE/TI): ");
                String tipoDoc = scanner.nextLine().trim();
                System.out.print("Número Documento: ");
                String numDoc = scanner.nextLine().trim();
                System.out.print("Nombres: ");
                String nombres = scanner.nextLine().trim();
                System.out.print("Apellidos: ");
                String apellidos = scanner.nextLine().trim();
                System.out.print("Correo Electrónico: ");
                String correo = scanner.nextLine().trim();
                System.out.print("Teléfono Principal (WhatsApp): ");
                String tel = scanner.nextLine().trim();
                System.out.print("Dirección: ");
                String dir = scanner.nextLine().trim();

                Cliente nuevo = new Cliente(tipoDoc, numDoc, nombres, apellidos, correo, dir, "Montería");
                Cliente guardado = clienteService.registrarCliente(nuevo, tel, "WHATSAPP");
                System.out.println("[✔] Cliente registrado exitosamente con ID: " + guardado.getIdCliente());
                break;
            case "c":
                System.out.print("Ingrese número de documento a buscar: ");
                String docBuscado = scanner.nextLine().trim();
                Optional<Cliente> opt = clienteService.buscarPorId(Integer.parseInt(docBuscado));
                if (opt.isPresent()) {
                    System.out.println(opt.get());
                } else {
                    System.out.println("Cliente no encontrado.");
                }
                break;
        }
    }

    private static void moduloGestionMascotas() throws Exception {
        System.out.println("\n--- [3] GESTIÓN DE MASCOTAS ---");
        System.out.print("Ingrese ID del cliente propietario: ");
        int idCliente = Integer.parseInt(scanner.nextLine().trim());

        List<Mascota> mascotas = clienteService.listarMascotasDeCliente(idCliente);
        System.out.println("Mascotas registradas para el cliente #" + idCliente + ":");
        if (mascotas.isEmpty()) {
            System.out.println("El cliente no tiene mascotas registradas.");
        } else {
            for (Mascota m : mascotas) {
                System.out.println(" • " + m);
            }
        }
    }

    private static void moduloGestionCitas() throws Exception {
        System.out.println("\n--- [4] GESTIÓN DE CITAS Y TURNOS ---");
        System.out.println("a. Listar citas");
        System.out.println("b. Agendar nueva cita (Genera Token Anti-IDOR)");
        System.out.print("Seleccione sub-opción [a-b]: ");
        String sub = scanner.nextLine().trim().toLowerCase();

        if ("a".equals(sub)) {
            List<Cita> citas = citaService.listarTodas();
            if (citas.isEmpty()) {
                System.out.println("No hay citas programadas.");
            } else {
                for (Cita c : citas) {
                    System.out.println(" • Cita #" + c.getIdCita() + " | Mascota: " + c.getNombreMascota() +
                            " | Servicio: " + c.getNombreServicio() + " | Estado: " + c.getEstado() +
                            " | Token: " + c.getTokenAcceso());
                }
            }
        } else if ("b".equals(sub)) {
            System.out.print("ID Cliente: ");
            int idCliente = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("ID Mascota: ");
            int idMascota = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("ID Servicio (1: Baño, 2: Corte, 3: Spa): ");
            int idServicio = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Valor Total ($): ");
            BigDecimal valor = new BigDecimal(scanner.nextLine().trim());
            System.out.print("Observaciones de recepción: ");
            String obs = scanner.nextLine().trim();

            Timestamp fechaHora = Timestamp.valueOf(LocalDateTime.now().plusDays(1));
            Cita cita = citaService.agendarCita(idCliente, idMascota, idServicio, fechaHora, valor, BigDecimal.ZERO, obs);

            System.out.println("[✔] Cita agendada con éxito.");
            System.out.println("    ID de Cita: " + cita.getIdCita());
            System.out.println("    Token Anti-IDOR asignado: " + cita.getTokenAcceso());
            System.out.println("    Enlace de Álbum para el cliente: https://canstyling.com/album?token=" + cita.getTokenAcceso());
        }
    }

    private static void moduloConsultarAlbumAntiIdor() throws Exception {
        System.out.println("\n--- [5] CONSULTA SEGURA DE ÁLBUM DIGITAL (ANTI-IDOR) ---");
        System.out.println("Nota: Se accede mediante el Token UUID único, sin exponer el ID correlativo de la cita.");
        System.out.print("Ingrese o pegue el Token de Acceso (ej. e4b2d18a-78f9-4c12-98ab-34cd56ef7890): ");
        String token = scanner.nextLine().trim();

        Optional<Cita> optCita = citaService.consultarAlbumPorToken(token);
        if (optCita.isPresent()) {
            Cita c = optCita.get();
            System.out.println("\n=======================================================");
            System.out.println("        🐾 FICHA DEL ÁLBUM DIGITAL - CANSTYLING         ");
            System.out.println("=======================================================");
            System.out.println(" Dueño: " + c.getNombreCliente());
            System.out.println(" Mascota: " + c.getNombreMascota());
            System.out.println(" Servicio: " + c.getNombreServicio());
            System.out.println(" Estado del servicio: " + c.getEstado());
            System.out.println(" Fecha: " + c.getFechaHora());
            System.out.println(" Observaciones: " + (c.getObservacionesEntrega() != null ? c.getObservacionesEntrega() : c.getObservacionesRecepcion()));
            System.out.println(" Fotos del Servicio:");
            if (c.getFotos().isEmpty()) {
                System.out.println("  (Aún no se han registrado fotos para este servicio)");
            } else {
                for (FotoMascota f : c.getFotos()) {
                    System.out.println("   • [" + f.getTipo() + "] " + f.getUrlFoto());
                }
            }
            System.out.println("=======================================================");
        } else {
            System.out.println("[✘] Token no encontrado o inválido. Acceso denegado.");
        }
    }

    private static void moduloLoginPorCorreoConOtp() throws Exception {
        System.out.println("\n--- [6] LOGIN POR CORREO ELECTRÓNICO CON CÓDIGO OTP (PASSWORDLESS) ---");
        System.out.print("Ingrese su correo electrónico registrado: ");
        String correo = scanner.nextLine().trim();

        try {
            System.out.println("Generando código OTP seguro...");
            CodigoOTP otp = authService.solicitarOtpLogin(correo);

            System.out.println("\n[✉] Simulación de Envío de Correo:");
            System.out.println("    Para: " + correo);
            System.out.println("    Código OTP de acceso: >> " + otp.getCodigoPlano() + " <<");
            System.out.println("    Vigencia: 5 minutos | Intentos permitidos: 3");

            System.out.print("\n➤ Ingrese el código de 6 dígitos recibido para autenticarse: ");
            String codigoIngresado = scanner.nextLine().trim();

            boolean autenticado = authService.verificarOtpLogin(correo, codigoIngresado);
            if (autenticado) {
                System.out.println("[✔] ¡Autenticación Satisfactoria! Sesión iniciada correctamente.");
            } else {
                System.out.println("[✘] Error de Autenticación: Código incorrecto, expirado o superó el límite de intentos.");
            }
        } catch (Exception e) {
            System.out.println("[✘] Error: " + e.getMessage());
        }
    }

    private static void ejecutarDemoIntegralAutomatica() {
        System.out.println("\n================================================================================");
        System.out.println("          EJECUTANDO DEMO INTEGRAL DE PRUEBAS AUTOMATIZADAS (CRUD Y SEGURIDAD)  ");
        System.out.println("================================================================================");

        try {
            System.out.println("1. Validando Conexión JDBC...");
            boolean dbOk = DatabaseConnection.getInstance().testConnection();
            System.out.println("   Resultado: " + (dbOk ? "CONECTADO A MYSQL" : "MODO MOCK/LOCAL"));

            System.out.println("\n2. Probando Criptografía y Seguridad:");
            String token = com.canstyling.util.SecurityUtils.generateAntiIdorToken();
            System.out.println("   • Token Anti-IDOR generado: " + token);

            String otpCode = com.canstyling.util.SecurityUtils.generateOtpCode(6);
            String otpHash = com.canstyling.util.SecurityUtils.hashSha256(otpCode);
            System.out.println("   • OTP generado (SecureRandom): " + otpCode);
            System.out.println("   • Hash SHA-256 del OTP: " + otpHash);

            String passHash = com.canstyling.util.SecurityUtils.hashPassword("Sena2026*");
            boolean passValida = com.canstyling.util.SecurityUtils.verifyPassword("Sena2026*", passHash);
            System.out.println("   • BCrypt Hash generado: " + passHash);
            System.out.println("   • Verificación de contraseña: " + (passValida ? "CORRECTA" : "FALLÓ"));

            System.out.println("\n3. Probando flujo de negocio en Base de Datos:");
            if (dbOk) {
                String testDoc = "TEST_" + System.currentTimeMillis() % 100000;
                String testEmail = "test" + System.currentTimeMillis() % 10000 + "@canstyling.com";
                Cliente c = new Cliente("CC", testDoc, "Cliente", "Prueba Auto", testEmail, "Calle Test", "Montería");
                Cliente nuevo = clienteService.registrarCliente(c, "3009998877", "WHATSAPP");
                System.out.println("   [CREATE] Cliente creado con ID: " + nuevo.getIdCliente());

                Optional<Cliente> consultado = clienteService.buscarPorId(nuevo.getIdCliente());
                System.out.println("   [READ] Cliente consultado: " + consultado.map(Cliente::getNombreCompleto).orElse("No hallado"));

                nuevo.setObservaciones("Cliente VIP actualizado en demo");
                boolean actualizado = clienteService.actualizarCliente(nuevo);
                System.out.println("   [UPDATE] Cliente actualizado: " + actualizado);

                boolean eliminado = clienteService.eliminarCliente(nuevo.getIdCliente());
                System.out.println("   [DELETE] Cliente eliminado (Limpieza): " + eliminado);
            } else {
                System.out.println("   (Omitiendo operaciones DML en BD física ya que MySQL no está respondiendo en localhost)");
            }

            System.out.println("\n[✔] Demostración Integral Finalizada con Éxito.");
        } catch (Exception e) {
            System.out.println("[✘] Error en demo: " + e.getMessage());
        }
    }
}
