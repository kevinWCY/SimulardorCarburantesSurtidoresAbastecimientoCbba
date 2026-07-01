// ExportadorExcel.java
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportadorExcel {

    public static void exportar(
            File archivo,
            Properties configuracion,
            ResumenSimulacion resumen,
            Simulador simulador,
            List<ResultadoVehiculo> resultados
    ) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archivo))) {
            escribir(zip, "[Content_Types].xml", tipos());
            escribir(zip, "_rels/.rels", relsRaiz());
            escribir(zip, "xl/workbook.xml", workbook());
            escribir(zip, "xl/_rels/workbook.xml.rels", relsWorkbook());
            escribir(zip, "xl/worksheets/sheet1.xml", hojaResumen(configuracion, resumen, simulador));
            escribir(zip, "xl/worksheets/sheet2.xml", hojaResultados(resultados));
        }
    }

    private static void escribir(ZipOutputStream zip, String nombre, String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String tipos() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet2.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "</Types>";
    }

    private static String relsRaiz() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
    }

    private static String workbook() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<sheets>"
                + "<sheet name=\"Resumen\" sheetId=\"1\" r:id=\"rId1\"/>"
                + "<sheet name=\"Resultados\" sheetId=\"2\" r:id=\"rId2\"/>"
                + "</sheets></workbook>";
    }

    private static String relsWorkbook() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet2.xml\"/>"
                + "</Relationships>";
    }

    private static String hojaResumen(Properties configuracion, ResumenSimulacion resumen, Simulador simulador) {
        Hoja hoja = new Hoja();
        hoja.fila("Parametros generales");
        hoja.fila("N vehiculos", configuracion.getProperty("general.n", ""));
        hoja.fila("Lambda", configuracion.getProperty("general.lambda", ""));
        hoja.fila("Cantidad de periodos", configuracion.getProperty("general.periodos", ""));
        hoja.fila("Periodicidad", configuracion.getProperty("general.periodicidad", ""));
        hoja.fila("Semilla", configuracion.getProperty("general.semilla", ""));
        hoja.vacia();

        hoja.fila("Precios por carburante");
        tablaConfiguracion(hoja, configuracion, "precio",
                new String[]{"Carburante", "Precio base subv.", "Incremento", "Precio internacional"});
        hoja.vacia();

        hoja.fila("Resumen operativo");
        hoja.fila("Vehiculos simulados", String.valueOf(resumen.getVehiculosSimulados()));
        hoja.fila("Atendidos", String.valueOf(resumen.getVehiculosAtendidos()));
        hoja.fila("No atendidos", String.valueOf(resumen.getVehiculosNoAtendidos()));
        hoja.fila("Ruta subvencionada", String.valueOf(resumen.getCantidadSubvencionada()));
        hoja.fila("Ruta internacional", String.valueOf(resumen.getCantidadInternacional()));
        hoja.fila("Espera promedio", f(resumen.getEsperaPromedio()));
        hoja.fila("Servicio promedio", f(resumen.getServicioPromedio()));
        hoja.fila("Tiempo total promedio", f(resumen.getTiempoTotalPromedio()));
        hoja.vacia();

        hoja.fila("Resumen economico");
        hoja.fila("Litros vendidos subvencionados", f(resumen.getLitrosVendidosSubvencionados()));
        hoja.fila("Litros vendidos internacionales", f(resumen.getLitrosVendidosInternacionales()));
        hoja.fila("Litros vendidos totales", f(resumen.getLitrosVendidosTotales()));
        hoja.fila("Ingreso subvencionado", f(resumen.getIngresoSubvencionado()));
        hoja.fila("Ingreso internacional", f(resumen.getIngresoInternacional()));
        hoja.fila("Ingreso total", f(resumen.getIngresoTotal()));
        hoja.fila("Litros no vendidos", f(resumen.getLitrosNoVendidos()));
        hoja.fila("Perdida estimada", f(resumen.getPerdidaEstimada()));
        hoja.vacia();

        hoja.fila("Resumen de abastecimiento");
        hoja.fila("Inventario inicial", f(resumen.getInventarioInicial()));
        hoja.fila("Inventario final", f(resumen.getInventarioFinal()));
        hoja.fila("Capacidad maxima", f(simulador.getCapacidadMaxima()));
        hoja.fila("Nivel minimo", f(simulador.getNivelMinimo()));
        hoja.fila("Cisternas recibidas", String.valueOf(resumen.getCisternasRecibidas()));
        hoja.fila("Litros abastecidos reales", f(resumen.getLitrosAbastecidosReales()));
        hoja.vacia();

        hoja.fila("Utilizacion de surtidores");
        hoja.fila("Surtidor", "Atendidos", "Utilizacion");
        for (String surtidor : resumen.getAtendidosPorSurtidor().keySet()) {
            hoja.fila(surtidor,
                    String.valueOf(resumen.getAtendidosPorSurtidor().get(surtidor)),
                    f(resumen.getUtilizacionPorSurtidor().get(surtidor)) + "%");
        }

        return hoja.xml();
    }

    private static void tablaConfiguracion(Hoja hoja, Properties configuracion, String prefijo, String[] encabezados) {
        hoja.fila(encabezados);
        int filas = Integer.parseInt(configuracion.getProperty(prefijo + ".filas", "0"));
        for (int fila = 0; fila < filas; fila++) {
            String[] valores = new String[encabezados.length];
            for (int columna = 0; columna < encabezados.length; columna++) {
                valores[columna] = configuracion.getProperty(prefijo + "." + fila + "." + columna, "");
            }
            hoja.fila(valores);
        }
    }

    private static String hojaResultados(List<ResultadoVehiculo> resultados) {
        Hoja hoja = new Hoja();
        hoja.fila(ExportadorResultados.ENCABEZADOS);
        for (ResultadoVehiculo resultado : resultados) {
            hoja.fila(ExportadorResultados.fila(resultado));
        }
        return hoja.xml();
    }

    private static String f(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private static String esc(String texto) {
        return (texto == null ? "" : texto)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static class Hoja {
        private final StringBuilder filas = new StringBuilder();
        private int filaActual = 1;

        void fila(String... valores) {
            filas.append("<row r=\"").append(filaActual).append("\">");
            for (int i = 0; i < valores.length; i++) {
                filas.append("<c r=\"").append(columna(i)).append(filaActual)
                        .append("\" t=\"inlineStr\"><is><t>")
                        .append(esc(valores[i]))
                        .append("</t></is></c>");
            }
            filas.append("</row>");
            filaActual++;
        }

        void vacia() {
            filaActual++;
        }

        String xml() {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                    + "<sheetData>" + filas + "</sheetData></worksheet>";
        }

        private String columna(int indice) {
            StringBuilder columna = new StringBuilder();
            int n = indice + 1;
            while (n > 0) {
                int r = (n - 1) % 26;
                columna.insert(0, (char) ('A' + r));
                n = (n - 1) / 26;
            }
            return columna.toString();
        }
    }
}
