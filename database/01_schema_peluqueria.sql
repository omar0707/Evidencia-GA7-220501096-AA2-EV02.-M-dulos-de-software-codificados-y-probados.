-- =========================================================================================
-- PROYECTO FORMATIVO SENA - CANSTYLING (PELUQUERIA CANINA)
-- Guia de Aprendizaje 07 - Evidencia: GA7-220501096-AA2-EV01
-- Archivo: 01_schema_peluqueria.sql
-- Descripcion: Esquema DDL Relacional con principios de seguridad Anti-IDOR,
--              soporte multi-telefono, tabla de OTP criptografico y auditoria.
-- Aprendiz: Omar Ulpiano Molina Malaver
-- Instructor: Adonay Sanchez Vertel
-- =========================================================================================

DROP DATABASE IF EXISTS peluqueria_canina_db;
CREATE DATABASE peluqueria_canina_db 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE peluqueria_canina_db;

-- -----------------------------------------------------------------------------------------
-- 1. TABLA: ROLES DE SISTEMA
-- -----------------------------------------------------------------------------------------
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE COMMENT 'ADMINISTRADOR, RECEPCIONISTA, PELUQUERO, CLIENTE',
    descripcion VARCHAR(255) NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Catalogo de roles del sistema';

-- -----------------------------------------------------------------------------------------
-- 2. TABLA: USUARIOS (ACCESO AL SISTEMA Y CREDENCIALES)
-- -----------------------------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NULL COMMENT 'Hash BCrypt (opcional si se autentica solo por OTP)',
    telefono VARCHAR(20) NULL,
    estado ENUM('ACTIVO', 'INACTIVO', 'BLOQUEADO') DEFAULT 'ACTIVO',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_roles FOREIGN KEY (id_rol) 
        REFERENCES roles(id_rol) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Usuarios administrativos y del sistema';

-- -----------------------------------------------------------------------------------------
-- 3. TABLA: CLIENTES (PROPIETARIOS DE MASCOTAS)
-- -----------------------------------------------------------------------------------------
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NULL UNIQUE,
    tipo_documento ENUM('CC', 'CE', 'TI', 'PASAPORTE', 'OTRO') DEFAULT 'CC',
    numero_documento VARCHAR(30) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    direccion VARCHAR(200) NULL,
    ciudad VARCHAR(100) DEFAULT 'Montería',
    estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO',
    observaciones TEXT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_clientes_usuarios FOREIGN KEY (id_usuario) 
        REFERENCES usuarios(id_usuario) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Propietarios de mascotas registrados';

-- -----------------------------------------------------------------------------------------
-- 4. TABLA: CLIENTE_TELEFONOS (SOPORTE MULTI-NUMERO Y WHATSAPP)
-- -----------------------------------------------------------------------------------------
CREATE TABLE cliente_telefonos (
    id_telefono INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    numero_telefono VARCHAR(20) NOT NULL,
    tipo ENUM('WHATSAPP', 'MOVIL', 'FIJO', 'OTRO') DEFAULT 'WHATSAPP',
    es_principal BOOLEAN DEFAULT FALSE,
    verificado BOOLEAN DEFAULT FALSE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_telefonos_cliente FOREIGN KEY (id_cliente) 
        REFERENCES clientes(id_cliente) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_cliente_numero UNIQUE (id_cliente, numero_telefono)
) ENGINE=InnoDB COMMENT='Multiples lineas telefonicas y WhatsApp por cliente';

-- -----------------------------------------------------------------------------------------
-- 5. TABLA: RAZAS CANINAS
-- -----------------------------------------------------------------------------------------
CREATE TABLE razas (
    id_raza INT AUTO_INCREMENT PRIMARY KEY,
    nombre_raza VARCHAR(100) NOT NULL UNIQUE,
    tamano ENUM('MINI', 'PEQUENO', 'MEDIANO', 'GRANDE', 'GIGANTE') NOT NULL,
    tipo_pelo ENUM('CORTO', 'MEDIO', 'LARGO', 'RIZADO', 'DOBLE_CAPA') NOT NULL,
    descripcion TEXT NULL
) ENGINE=InnoDB COMMENT='Catalogo de razas caninas';

-- -----------------------------------------------------------------------------------------
-- 6. TABLA: MASCOTAS
-- -----------------------------------------------------------------------------------------
CREATE TABLE mascotas (
    id_mascota INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_raza INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    genero ENUM('MACHO', 'HEMBRA') NOT NULL,
    edad_anos INT DEFAULT 0,
    peso_kg DECIMAL(5,2) NULL,
    color VARCHAR(100) NULL,
    alergias_condiciones TEXT NULL,
    agresivo BOOLEAN DEFAULT FALSE,
    estado ENUM('ACTIVO', 'FALLECIDO', 'INACTIVO') DEFAULT 'ACTIVO',
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_mascotas_cliente FOREIGN KEY (id_cliente) 
        REFERENCES clientes(id_cliente) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_mascotas_raza FOREIGN KEY (id_raza) 
        REFERENCES razas(id_raza) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Mascotas registradas en el sistema';

-- -----------------------------------------------------------------------------------------
-- 7. TABLA: SERVICIOS
-- -----------------------------------------------------------------------------------------
CREATE TABLE servicios (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre_servicio VARCHAR(100) NOT NULL,
    descripcion TEXT NULL,
    precio_base DECIMAL(10,2) NOT NULL,
    duracion_minutos INT NOT NULL DEFAULT 60,
    estado ENUM('ACTIVO', 'INACTIVO') DEFAULT 'ACTIVO'
) ENGINE=InnoDB COMMENT='Catalogo de servicios de peluqueria e higiene canina';

-- -----------------------------------------------------------------------------------------
-- 8. TABLA: CITAS (CON TOKEN DE ACCESO ANTI-IDOR)
-- -----------------------------------------------------------------------------------------
CREATE TABLE citas (
    id_cita INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_mascota INT NOT NULL,
    id_servicio INT NOT NULL,
    id_peluquero INT NULL,
    fecha_hora DATETIME NOT NULL,
    estado ENUM('PENDIENTE', 'CONFIRMADA', 'EN_PROCESO', 'COMPLETADA', 'CANCELADA') DEFAULT 'PENDIENTE',
    valor_total DECIMAL(10,2) NOT NULL,
    descuento_aplicado DECIMAL(10,2) DEFAULT 0.00,
    token_acceso VARCHAR(64) NOT NULL UNIQUE COMMENT 'Token UUID Anti-IDOR para consulta publica del album',
    observaciones_recepcion TEXT NULL,
    observaciones_entrega TEXT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_citas_cliente FOREIGN KEY (id_cliente) 
        REFERENCES clientes(id_cliente) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_citas_mascota FOREIGN KEY (id_mascota) 
        REFERENCES mascotas(id_mascota) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_citas_servicio FOREIGN KEY (id_servicio) 
        REFERENCES servicios(id_servicio) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_citas_peluquero FOREIGN KEY (id_peluquero) 
        REFERENCES usuarios(id_usuario) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Registro de turnos y citas';

-- -----------------------------------------------------------------------------------------
-- 9. TABLA: FOTOS_MASCOTAS (REGISTRO ANTES / DESPUES)
-- -----------------------------------------------------------------------------------------
CREATE TABLE fotos_mascotas (
    id_foto INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NOT NULL,
    tipo ENUM('ANTES', 'DESPUES') NOT NULL,
    url_foto VARCHAR(500) NOT NULL,
    cloudinary_public_id VARCHAR(150) NULL,
    tamano_bytes BIGINT NULL,
    mime_type VARCHAR(50) DEFAULT 'image/jpeg',
    subido_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fotos_cita FOREIGN KEY (id_cita) 
        REFERENCES citas(id_cita) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='Fotos del servicio para el album del cliente';

-- -----------------------------------------------------------------------------------------
-- 10. TABLA: CODIGOS_OTP (AUTENTICACION SEGURA Y LOGIN SIN CONTRASEÑA)
-- -----------------------------------------------------------------------------------------
CREATE TABLE codigos_otp (
    id_otp INT AUTO_INCREMENT PRIMARY KEY,
    destino VARCHAR(150) NOT NULL COMMENT 'Correo electronico o numero telefonico',
    codigo_hash VARCHAR(255) NOT NULL COMMENT 'Hash del codigo OTP generado con SecureRandom',
    tipo_proposito ENUM('LOGIN', 'REGISTRO', 'VERIFICACION_WHATSAPP', 'CONFIRMAR_CITA') DEFAULT 'LOGIN',
    intentos_fallidos INT DEFAULT 0,
    usado BOOLEAN DEFAULT FALSE,
    expira_en DATETIME NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_otp_destino_expira (destino, expira_en, usado)
) ENGINE=InnoDB COMMENT='Bitacora y validacion de codigos de un solo uso (OTP)';
