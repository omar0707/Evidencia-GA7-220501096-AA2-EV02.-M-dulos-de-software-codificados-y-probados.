-- =========================================================================================
-- PROYECTO FORMATIVO SENA - CANSTYLING (PELUQUERIA CANINA)
-- Guia de Aprendizaje 07 - Evidencia: GA7-220501096-AA2-EV01
-- Archivo: 03_security_user.sql
-- Descripcion: Principio de Menor Privilegio (Least Privilege Principle).
--              Crea un usuario restringido para la aplicacion Java que solo posee permisos
--              DML (SELECT, INSERT, UPDATE, DELETE) y no permisos destructivos de DDL (DROP, ALTER).
-- =========================================================================================

-- 1. Crear usuario dedicado para la conexion de la aplicacion
CREATE USER IF NOT EXISTS 'usr_canstyling'@'localhost' IDENTIFIED BY 'CanStyling2026Secure!';
CREATE USER IF NOT EXISTS 'usr_canstyling'@'%' IDENTIFIED BY 'CanStyling2026Secure!';

-- 2. Otorgar UNICAMENTE permisos DML sobre la base de datos de la peluqueria
GRANT SELECT, INSERT, UPDATE, DELETE ON peluqueria_canina_db.* TO 'usr_canstyling'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON peluqueria_canina_db.* TO 'usr_canstyling'@'%';

-- 3. Aplicar cambios de privilegios
FLUSH PRIVILEGES;

-- 4. Verificacion de privilegios otorgados
SHOW GRANTS FOR 'usr_canstyling'@'localhost';
