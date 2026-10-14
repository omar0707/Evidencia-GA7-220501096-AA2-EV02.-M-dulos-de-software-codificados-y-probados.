package com.canstyling.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor centralizado de variables de entorno del sistema.
 * Lee variables del archivo .env local (si existe) y del entorno del sistema operativo (System.getenv).
 * Implementado con Java estandar para garantizar portabilidad total sin dependencias externas obligatorias.
 */
public class EnvConfig {

    private static final Map<String, String> envMap = new HashMap<>();

    static {
        cargarArchivoDotEnv();
    }

    private static void cargarArchivoDotEnv() {
        String[] posiblesRutas = {
                ".env",
                "../.env",
                Paths.get("").toAbsolutePath().toString() + File.separator + ".env"
        };

        for (String ruta : posiblesRutas) {
            File f = new File(ruta);
            if (f.exists() && f.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        linea = linea.trim();
                        if (linea.isEmpty() || linea.startsWith("#")) {
                            continue;
                        }
                        int sep = linea.indexOf('=');
                        if (sep > 0) {
                            String key = linea.substring(0, sep).trim();
                            String val = linea.substring(sep + 1).trim();
                            // Remover comillas si existen
                            if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                                val = val.substring(1, val.length() - 1);
                            }
                            envMap.put(key, val);
                        }
                    }
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static String get(String key, String defaultValue) {
        if (envMap.containsKey(key)) {
            String val = envMap.get(key);
            if (val != null && !val.trim().isEmpty()) {
                return val;
            }
        }
        String sysEnv = System.getenv(key);
        if (sysEnv != null && !sysEnv.trim().isEmpty()) {
            return sysEnv;
        }
        return defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String val = get(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
