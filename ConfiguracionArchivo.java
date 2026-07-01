// ConfiguracionArchivo.java
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfiguracionArchivo {

    public static void guardar(File archivo, Properties propiedades) throws IOException {
        try (FileOutputStream salida = new FileOutputStream(archivo)) {
            propiedades.store(salida, "Configuracion del simulador de surtidores");
        }
    }

    public static Properties cargar(File archivo) throws IOException {
        Properties propiedades = new Properties();
        try (FileInputStream entrada = new FileInputStream(archivo)) {
            propiedades.load(entrada);
        }
        return propiedades;
    }
}
