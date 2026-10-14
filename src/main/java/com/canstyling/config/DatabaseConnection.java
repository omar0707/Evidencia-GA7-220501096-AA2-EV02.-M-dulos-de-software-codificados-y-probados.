package com.canstyling.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor de Conexion a Base de Datos MySQL aplicando el Patron Singleton.
 * Asegura la carga segura del driver JDBC, conexion con UTF-8 y manejo de transacciones.
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        // Constructor privado para patron Singleton
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Obtiene una conexion activa a la base de datos MySQL.
     * Si la conexion actual fue cerrada o es invalida, establece una nueva.
     */
    public synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                String host = EnvConfig.get("DB_HOST", "localhost");
                String port = EnvConfig.get("DB_PORT", "3306");
                String dbName = EnvConfig.get("DB_NAME", "peluqueria_canina_db");
                String user = EnvConfig.get("DB_USER", "root");
                String password = EnvConfig.get("DB_PASSWORD", "");

                String jdbcUrl = String.format(
                        "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota&characterEncoding=UTF-8",
                        host, port, dbName
                );

                // Cargar Driver JDBC explicitamente para compatibilidad
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.SEVERE, "Driver de MySQL no encontrado en el classpath", e);
                }

                connection = DriverManager.getConnection(jdbcUrl, user, password);
                LOGGER.info("Conexión JDBC a MySQL establecida exitosamente con el esquema: " + dbName);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al conectar con la base de datos MySQL: " + e.getMessage(), e);
            throw e;
        }
        return connection;
    }

    /**
     * Verifica si la conexion a la base de datos responde.
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.warning("Test de conexión fallido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cierra la conexion activa si existe.
     */
    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    LOGGER.info("Conexión JDBC cerrada de manera segura.");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error al cerrar la conexión JDBC", e);
            } finally {
                connection = null;
            }
        }
    }
}
