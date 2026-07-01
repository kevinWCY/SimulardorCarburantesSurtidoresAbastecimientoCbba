// ExportadorCsv.java
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ExportadorCsv {

    public static void exportar(File archivo, List<ResultadoVehiculo> resultados) throws IOException {
        StringBuilder csv = new StringBuilder();
        escribirFila(csv, ExportadorResultados.ENCABEZADOS);

        for (ResultadoVehiculo resultado : resultados) {
            escribirFila(csv, ExportadorResultados.fila(resultado));
        }

        Files.write(archivo.toPath(), csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void escribirFila(StringBuilder csv, String[] valores) {
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                csv.append(';');
            }
            csv.append(escapar(valores[i]));
        }
        csv.append(System.lineSeparator());
    }

    private static String escapar(String valor) {
        String texto = valor == null ? "" : valor;
        boolean requiereComillas = texto.contains(";")
                || texto.contains("\"")
                || texto.contains("\n")
                || texto.contains("\r");

        texto = texto.replace("\"", "\"\"");
        return requiereComillas ? "\"" + texto + "\"" : texto;
    }
}
