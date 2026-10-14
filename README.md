# Evidencia GA7-220501096-AA2-EV01: Codificacion de Modulos del Software

**Programa de Formacion:** Analisis y Desarrollo de Software (ADSO)  
**Ficha:** 3186645  
**Aprendiz:** Omar Ulpiano Molina Malaver  
**Instructor:** Adonay Sanchez Vertel  
**Centro de Formacion:** Centro Agropecuario y de Biotecnologia El Porvenir (SENA - Regional Cordoba / Monteria)  
**Proyecto:** Sistema de Gestion para Peluqueria Canina - *CanStyling*  
**Fecha:** Septiembre de 2026  

---

## 1. Descripcion de la Evidencia

La presente evidencia corresponde al diseño, codificacion y estructuracion del **modulo de software base de backend** para el proyecto *CanStyling*, realizando conexiones con bases de datos relacionales por medio de **JDBC (Java Database Connectivity)** y **MySQL**, aplicando los estandares de la industria, principios de arquitectura en capas (DAO / MVC) y mecanismos estrictos de seguridad de la informacion.

### Aspectos Clave Implementados:
1. **Conexion a Base de Datos por JDBC**: Implementacion con el patron de diseño **Singleton** (`DatabaseConnection.java`), gestion segura de credenciales mediante variables de entorno (`.env`) y manejo riguroso de excepciones `SQLException`.
2. **Proteccion Anti-SQL Injection**: El 100% de las operaciones DML (insercion, consulta, actualizacion y eliminacion) estan implementadas exclusivamente con **`PreparedStatement` parametrizados**.
3. **Seguridad Anti-IDOR (Insecure Direct Object References)**: La tabla de citas implementa un token criptografico impredecible `token_acceso VARCHAR(64) UNIQUE` (UUID v4) para permitir el acceso al album digital del cliente sin exponer identificadores autonumericos secuenciales (1, 2, 3...).
4. **Soporte Multi-Telefono y WhatsApp**: Estructura relacional `cliente_telefonos` que permite asociar multiples numeros y lineas de WhatsApp a un mismo cliente.
5. **Autenticacion Segura y Login Passwordless por Correo con OTP**:
   - Generacion de codigos numericos de 6 digitos mediante `java.security.SecureRandom`.
   - Vigencia y tiempo de expiracion estricto de 5 minutos.
   - Limite de 3 intentos fallidos antes del bloqueo automatico.
   - Consumo de un solo uso (`usado = true`).
   - Rate limit / Cooldown de 60 segundos entre solicitudes hacia el mismo correo.
6. **Principio de Menor Privilegio (Least Privilege)**: Script SQL dedicado (`03_security_user.sql`) que crea un usuario de base de datos con permisos acotados (`SELECT, INSERT, UPDATE, DELETE`) sin privilegios administrativos destructivos (`DROP, ALTER, GRANT`).

---

## 2. Estructura del Proyecto

```
GA7-220501096-AA2-EV01_Codificacion_Modulos_JDBC/
│
├── pom.xml                                        # Descriptor Maven con dependencias oficiales
├── .env.example                                   # Plantilla de variables de entorno
├── .env                                           # Configuracion local de base de datos
├── .gitignore                                     # Exclusion de credenciales y artefactos compilados
├── README.md                                      # Documentacion tecnica y sustentacion
│
├── database/                                      # Scripts SQL del modelo relacional
│   ├── 01_schema_peluqueria.sql                   # Esquema DDL con Anti-IDOR, multi-telefono y OTP
│   ├── 02_seed_data.sql                           # Semilla de datos de prueba
│   └── 03_security_user.sql                       # Usuario de menor privilegio para MySQL
│
└── src/
    ├── main/java/com/canstyling/
    │   ├── config/
    │   │   ├── DatabaseConnection.java            # Conexion JDBC Singleton y control de transacciones
    │   │   └── EnvConfig.java                     # Lector de variables de entorno (.env)
    │   ├── model/
    │   │   ├── Cliente.java                       # Entidad Cliente
    │   │   ├── ClienteTelefono.java               # Entidad Multi-Telefono / WhatsApp
    │   │   ├── Mascota.java                       # Entidad Mascota (Raza, edad, salud)
    │   │   ├── Raza.java                          # Catalogo de razas caninas
    │   │   ├── Servicio.java                      # Catalogo de servicios y precios
    │   │   ├── Cita.java                          # Citas y turnos con token Anti-IDOR
    │   │   ├── FotoMascota.java                   # Fotos Antes / Despues
    │   │   ├── CodigoOTP.java                     # Entidad de Codigos OTP
    │   │   └── Usuario.java                       # Usuarios administrativos
    │   ├── dao/
    │   │   ├── GenericDAO.java                    # Interfaz CRUD generica
    │   │   ├── ClienteDAO.java                    # Contrato DAO Clientes
    │   │   ├── MascotaDAO.java                    # Contrato DAO Mascotas
    │   │   ├── CitaDAO.java                       # Contrato DAO Citas
    │   │   ├── CodigoOtpDAO.java                  # Contrato DAO Codigos OTP
    │   │   └── UsuarioDAO.java                    # Contrato DAO Usuarios
    │   ├── dao/impl/
    │   │   ├── ClienteDAOImpl.java                # Implementacion JDBC con PreparedStatement
    │   │   ├── MascotaDAOImpl.java                # Implementacion JDBC Mascotas
    │   │   ├── CitaDAOImpl.java                   # Implementacion JDBC Citas y Tokens
    │   │   ├── CodigoOtpDAOImpl.java              # Implementacion JDBC OTP y Expiracion
    │   │   └── UsuarioDAOImpl.java                # Implementacion JDBC Usuarios
    │   ├── service/
    │   │   ├── ClienteService.java                # Reglas de negocio de clientes
    │   │   ├── CitaService.java                   # Gestion de turnos y anti-citas fantasma
    │   │   ├── OtpService.java                    # Logica de seguridad y cooldown de OTP
    │   │   └── AuthService.java                   # Login por correo con OTP y BCrypt
    │   ├── util/
    │   │   └── SecurityUtils.java                 # Criptografia: UUID Anti-IDOR, SecureRandom, BCrypt
    │   └── Main.java                              # Consola interactiva para pruebas del sistema
    └── test/java/com/canstyling/
        └── ModuloSoftwareTest.java                # Suite de pruebas unitarias y de integracion
```

---

## 3. Estandares de Codificacion Aplicados

Siguiendo las directrices del componente formativo y las mejores practicas de la industria en Java:
* **Nombramiento de Paquetes:** En minusculas y siguiendo la convencion de dominio inverso (`com.canstyling.config`, `com.canstyling.dao`, `com.canstyling.model`, `com.canstyling.service`, `com.canstyling.util`).
* **Nombramiento de Clases:** *PascalCase* con nombres sustantivos claros y representativos (`DatabaseConnection`, `ClienteDAOImpl`, `SecurityUtils`).
* **Nombramiento de Metodos:** *camelCase* con verbos que indican la accion ejecutada (`registrarCliente`, `consultarAlbumPorToken`, `generarOtp`).
* **Nombramiento de Variables y Constantes:** Variables en *camelCase* y constantes en *UPPER_SNAKE_CASE* (`MAX_CITAS_ACTIVAS_POR_CLIENTE`).
* **Encapsulamiento:** Atributos privados con metodos de acceso `getters` y `setters` publicos y constructores parametrizados.

---

## 4. Matriz de Operaciones CRUD Implementadas

| Modulo | Create (Insercion) | Read (Consulta) | Update (Actualizacion) | Delete (Eliminacion) |
| :--- | :--- | :--- | :--- | :--- |
| **Clientes** | `create(Cliente)` con validacion de documento y correo unico | `findById`, `findByDocumento`, `findByCorreo`, `findByTelefono`, `findAll` | `update(Cliente)` actualiza datos de contacto y direccion | `deleteById(id)` borrado controlado |
| **Telefonos** | `addTelefono(ClienteTelefono)` con soporte WhatsApp | `findTelefonosByClienteId(id)` | Actualiza flag de principal o verificado | `deleteTelefono(id)` |
| **Mascotas** | `create(Mascota)` vinculada a cliente y raza | `findById`, `findByClienteId`, `findAll` | `update(Mascota)` actualiza peso, edad y notas | `deleteById(id)` |
| **Citas / Turnos** | `create(Cita)` asignando **Token UUID Anti-IDOR** | `findById`, `findByTokenAcceso`, `findByEstado`, `findByClienteId` | `update(Cita)` actualiza estado a `COMPLETADA` | `deleteById(id)` |
| **OTP / Auth** | `save(CodigoOTP)` con hash SHA-256 y vigencia 5 min | `findLatestActiveByDestino` | `markAsUsed`, `incrementIntentosFallidos` | `invalidatePreviousOtps` |

---

## 5. Manual de Instalacion y Ejecucion

### Requisitos Previos:
* Java Development Kit (JDK 17 o superior).
* Servidor MySQL (XAMPP, WampServer o MySQL Server nativo).

### Paso 1: Configurar la Base de Datos en MySQL
1. Inicie su servidor MySQL (por ejemplo, desde el panel de control de XAMPP).
2. Abra su cliente de base de datos favorito (phpMyAdmin, MySQL Workbench o DBeaver).
3. Ejecute secuencialmente los scripts ubicados en la carpeta `database/`:
   ```sql
   -- 1. Crear la base de datos y tablas
   SOURCE database/01_schema_peluqueria.sql;

   -- 2. Insertar datos de prueba
   SOURCE database/02_seed_data.sql;

   -- 3. Crear usuario de menor privilegio (opcional para produccion)
   SOURCE database/03_security_user.sql;
   ```

### Paso 2: Configurar las Credenciales
Verifique o edite el archivo `.env` en la raiz del proyecto para que coincida con las credenciales de su MySQL local:
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=peluqueria_canina_db
DB_USER=root
DB_PASSWORD=
```

### Paso 3: Compilacion y Ejecucion
Para compilar y ejecutar la aplicacion interactiva:
```bash
# Compilar clases Java
javac -d target/classes src/main/java/com/canstyling/**/*.java

# Ejecutar el menu principal por consola
java -cp "target/classes;lib/*" com.canstyling.Main
```
*(Tambien puede importarse directamente en IntelliJ IDEA, Eclipse o VS Code como proyecto Maven estandar).*

---

## 6. Sustentacion de Pruebas y Resultados

La aplicacion cuenta con una opcion de menu `[7]` y una suite de pruebas `ModuloSoftwareTest.java` que valida:
* Conexion exitosa mediante driver `mysql-connector-j`.
* Generacion de tokens UUID version 4 aleatorios y seguros.
* Generacion de codigos OTP con `SecureRandom` y hashing seguro.
* Cifrado y validacion de contraseñas con `BCrypt`.
* Flujo CRUD completo de insercion, lectura, modificacion y eliminacion en MySQL con sentencias preparadas.
