-- =========================================================================================
-- PROYECTO FORMATIVO SENA - CANSTYLING (PELUQUERIA CANINA)
-- Guia de Aprendizaje 07 - Evidencia: GA7-220501096-AA2-EV01
-- Archivo: 02_seed_data.sql
-- Descripcion: Datos iniciales y de prueba (Roles, Razas, Servicios, Clientes, Citas).
-- =========================================================================================

USE peluqueria_canina_db;

-- 1. Insercion de Roles
INSERT INTO roles (nombre_rol, descripcion) VALUES
('ADMINISTRADOR', 'Control total del sistema y reportes'),
('RECEPCIONISTA', 'Gestión de citas, clientes y facturación'),
('PELUQUERO', 'Operario de estética canina y registro fotográfico'),
('CLIENTE', 'Propietario de mascota con acceso a álbum');

-- 2. Insercion de Razas
INSERT INTO razas (nombre_raza, tamano, tipo_pelo, descripcion) VALUES
('Poodle (Caniche)', 'PEQUENO', 'RIZADO', 'Pelo rizado continuo, requiere corte de tijera y mantenimiento regular'),
('Golden Retriever', 'GRANDE', 'DOBLE_CAPA', 'Manto abundante con subpelo, requiere deslanado profundo'),
('Shih Tzu', 'PEQUENO', 'LARGO', 'Manto largo y sedoso, necesita cepillado diario y nudos deshechos'),
('Bulldog Francés', 'PEQUENO', 'CORTO', 'Pelo corto y piel sensible, requiere baño medicado y limpieza de pliegues'),
('Schnauzer Miniatura', 'PEQUENO', 'DOBLE_CAPA', 'Corte de raza con faldón y barba distintiva');

-- 3. Insercion de Servicios
INSERT INTO servicios (nombre_servicio, descripcion, precio_base, duracion_minutos) VALUES
('Baño Básico e Higiene', 'Baño con shampoo hipoalergénico, secado rápido, corte de uñas y limpieza de oídos', 35000.00, 45),
('Corte Comercial y Estilizado', 'Baño completo + corte de pelo con máquina y acabado a tijera según raza', 55000.00, 75),
('Spa Canino y Deslanado', 'Tratamiento de hidratación profunda, ozonoterapia y retiro de pelo muerto', 70000.00, 90),
('Profilaxis Dental Preventiva', 'Limpieza y cepillado dental con spray enzimático', 25000.00, 30);

-- 4. Insercion de Usuarios de Prueba (Password: Admin123* en BCrypt)
INSERT INTO usuarios (id_rol, nombres, apellidos, correo, password_hash, telefono, estado) VALUES
(1, 'Omar Ulpiano', 'Molina Malaver', 'admin@canstyling.com', '$2a$12$e8YVwQ8.bB0K/k4f7x8iCeY.eWwL0W4T4C7aE2jO1tI6kY/s.gQ3u', '3101234567', 'ACTIVO'),
(3, 'Carlos', 'Gómez Peluquero', 'carlos.peluquero@canstyling.com', '$2a$12$e8YVwQ8.bB0K/k4f7x8iCeY.eWwL0W4T4C7aE2jO1tI6kY/s.gQ3u', '3119876543', 'ACTIVO');

-- 5. Insercion de Clientes
INSERT INTO clientes (tipo_documento, numero_documento, nombres, apellidos, correo, direccion, ciudad) VALUES
('CC', '1067890123', 'Laura', 'Martínez Pérez', 'laura.martinez@gmail.com', 'Calle 44 # 12-30', 'Montería'),
('CC', '1067890456', 'Andrés', 'Sánchez Doria', 'andres.sanchez@hotmail.com', 'Cra 6 # 28-15', 'Montería'),
('CC', '1067890789', 'María', 'Fernanda Restrepo', 'mafe.restrepo@gmail.com', 'Barrio El Recreo Calle 60 # 4-20', 'Montería');

-- 6. Insercion de Telefonos Multiples (Soporte WhatsApp)
INSERT INTO cliente_telefonos (id_cliente, numero_telefono, tipo, es_principal, verificado) VALUES
(1, '3001112233', 'WHATSAPP', TRUE, TRUE),
(1, '3145556677', 'MOVIL', FALSE, FALSE),
(2, '3012223344', 'WHATSAPP', TRUE, TRUE),
(3, '3023334455', 'WHATSAPP', TRUE, TRUE),
(3, '0347890011', 'FIJO', FALSE, FALSE);

-- 7. Insercion de Mascotas
INSERT INTO mascotas (id_cliente, id_raza, nombre, genero, edad_anos, peso_kg, color, alergias_condiciones, agresivo) VALUES
(1, 1, 'Toby', 'MACHO', 3, 5.50, 'Blanco', 'Piel sensible a perfumes fuertes', FALSE),
(2, 2, 'Max', 'MACHO', 4, 32.00, 'Dorado', 'Ninguna conocida', FALSE),
(3, 5, 'Luna', 'HEMBRA', 2, 6.80, 'Sal y Pimienta', 'Sensible al corte en patitas delanteras', FALSE);

-- 8. Insercion de Citas con Tokens Anti-IDOR (UUID)
INSERT INTO citas (id_cliente, id_mascota, id_servicio, id_peluquero, fecha_hora, estado, valor_total, descuento_aplicado, token_acceso, observaciones_recepcion) VALUES
(1, 1, 2, 2, '2026-09-22 09:00:00', 'CONFIRMADA', 55000.00, 0.00, 'e4b2d18a-78f9-4c12-98ab-34cd56ef7890', 'Corte bajito en el lomo y pompones en las patas'),
(2, 2, 3, 2, '2026-09-22 11:00:00', 'PENDIENTE', 70000.00, 0.00, 'a1f8c9e2-4b3d-42e1-87ef-12ab34cd5678', 'Deslanado urgente por cambio de temporada'),
(3, 3, 1, 2, '2026-09-20 15:00:00', 'COMPLETADA', 35000.00, 0.00, 'c9d8e7f6-5a4b-3c2d-1e0f-9876543210ab', 'Servicio realizado con éxito y cliente satisfecho');

-- 9. Insercion de Fotos de Prueba
INSERT INTO fotos_mascotas (id_cita, tipo, url_foto, cloudinary_public_id, tamano_bytes, mime_type) VALUES
(3, 'ANTES', 'https://res.cloudinary.com/canstyling/image/upload/v1/antes_luna.jpg', 'canstyling/antes_luna', 1245000, 'image/jpeg'),
(3, 'DESPUES', 'https://res.cloudinary.com/canstyling/image/upload/v1/despues_luna.jpg', 'canstyling/despues_luna', 1380000, 'image/jpeg');
