// ExportadorDocx.java
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;

public class ExportadorDocx {

    public static void exportar(
            File archivo,
            Properties configuracion,
            ResumenSimulacion resumen,
            Simulador simulador,
            List<ResultadoVehiculo> resultados
    ) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archivo))) {
            escribir(zip, "[Content_Types].xml", contenidoTipos());
            escribir(zip, "_rels/.rels", relacionesRaiz());
            escribir(zip, "word/_rels/document.xml.rels", relacionesDocumento());
            escribir(zip, "word/document.xml", documento(configuracion, resumen, simulador, resultados));
        }
    }

    private static void escribir(ZipOutputStream zip, String nombre, String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String contenidoTipos() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>"
                + "</Types>";
    }

    private static String relacionesRaiz() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>"
                + "</Relationships>";
    }

    private static String relacionesDocumento() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"/>";
    }

    private static String documento(
            Properties configuracion,
            ResumenSimulacion resumen,
            Simulador simulador,
            List<ResultadoVehiculo> resultados
    ) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        xml.append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"><w:body>");

        titulo(xml, "Reporte de simulacion de surtidores");
        parrafo(xml, "Fecha y hora de exportacion: "
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        titulo2(xml, "Parametros generales");
        tabla(xml, new String[][]{
                {"N vehiculos", configuracion.getProperty("general.n", "")},
                {"Lambda", configuracion.getProperty("general.lambda", "")},
                {"Cantidad de periodos", configuracion.getProperty("general.periodos", "")},
                {"Periodicidad", configuracion.getProperty("general.periodicidad", "")},
                {"Semilla", configuracion.getProperty("general.semilla", "")}
        });

        titulo2(xml, "Precios por carburante");
        tablaDesdeConfiguracion(xml, configuracion, "precio",
                new String[]{"Carburante", "Precio base subv.", "Incremento", "Precio internacional"});

        titulo2(xml, "Perfiles de vehiculos");
        tablaDesdeConfiguracion(xml, configuracion, "perfil",
                new String[]{"Perfil", "L min", "L max", "TS min", "TS max", "CO Bs/h"});

        titulo2(xml, "Parametros de abastecimiento");
        tabla(xml, new String[][]{
                {"Inventario inicial", configuracion.getProperty("abastecimiento.inventarioInicial", "")},
                {"Capacidad maxima", configuracion.getProperty("abastecimiento.capacidadMaxima", "")},
                {"Nivel minimo", configuracion.getProperty("abastecimiento.nivelMinimo", "")},
                {"Tiempo cisterna min", configuracion.getProperty("abastecimiento.tiempoCisternaMin", "")},
                {"Tiempo cisterna max", configuracion.getProperty("abastecimiento.tiempoCisternaMax", "")},
                {"Carga cisterna min", configuracion.getProperty("abastecimiento.cargaCisternaMin", "")},
                {"Carga cisterna max", configuracion.getProperty("abastecimiento.cargaCisternaMax", "")},
                {"Descarga min", configuracion.getProperty("abastecimiento.descargaMin", "")},
                {"Descarga max", configuracion.getProperty("abastecimiento.descargaMax", "")}
        });

        titulo2(xml, "Resumen operativo");
        tabla(xml, new String[][]{
                {"Vehiculos simulados", String.valueOf(resumen.getVehiculosSimulados())},
                {"Atendidos", String.valueOf(resumen.getVehiculosAtendidos())},
                {"No atendidos", String.valueOf(resumen.getVehiculosNoAtendidos())},
                {"Ruta subvencionada", String.valueOf(resumen.getCantidadSubvencionada())},
                {"Ruta internacional", String.valueOf(resumen.getCantidadInternacional())},
                {"Espera promedio", f(resumen.getEsperaPromedio())},
                {"Servicio promedio", f(resumen.getServicioPromedio())},
                {"Tiempo total promedio", f(resumen.getTiempoTotalPromedio())}
        });

        titulo2(xml, "Resumen economico");
        tabla(xml, new String[][]{
                {"Litros vendidos subvencionados", f(resumen.getLitrosVendidosSubvencionados())},
                {"Litros vendidos internacionales", f(resumen.getLitrosVendidosInternacionales())},
                {"Litros vendidos totales", f(resumen.getLitrosVendidosTotales())},
                {"Ingreso subvencionado", f(resumen.getIngresoSubvencionado())},
                {"Ingreso internacional", f(resumen.getIngresoInternacional())},
                {"Ingreso total", f(resumen.getIngresoTotal())},
                {"Litros no vendidos", f(resumen.getLitrosNoVendidos())},
                {"Perdida estimada", f(resumen.getPerdidaEstimada())}
        });

        titulo2(xml, "Resumen de abastecimiento");
        tabla(xml, new String[][]{
                {"Inventario inicial", f(resumen.getInventarioInicial())},
                {"Inventario final", f(resumen.getInventarioFinal())},
                {"Capacidad maxima", f(simulador.getCapacidadMaxima())},
                {"Nivel minimo", f(simulador.getNivelMinimo())},
                {"Cisternas recibidas", String.valueOf(resumen.getCisternasRecibidas())},
                {"Litros abastecidos reales", f(resumen.getLitrosAbastecidosReales())}
        });

        titulo2(xml, "Utilizacion de surtidores");
        tablaUtilizacion(xml, resumen);

        titulo2(xml, "Resultados por vehiculo");
        tablaResultados(xml, resultados);
        parrafo(xml, "Para revisar la tabla completa de vehiculos simulados, exporte los resultados en CSV o Excel.");

        xml.append("<w:sectPr><w:pgSz w:w=\"16838\" w:h=\"11906\" w:orient=\"landscape\"/>");
        xml.append("<w:pgMar w:top=\"720\" w:right=\"720\" w:bottom=\"720\" w:left=\"720\"/></w:sectPr>");
        xml.append("</w:body></w:document>");
        return xml.toString();
    }

    private static void titulo(StringBuilder xml, String texto) {
        xml.append("<w:p><w:r><w:rPr><w:b/><w:sz w:val=\"32\"/></w:rPr><w:t>")
                .append(esc(texto)).append("</w:t></w:r></w:p>");
    }

    private static void titulo2(StringBuilder xml, String texto) {
        xml.append("<w:p><w:r><w:rPr><w:b/><w:sz w:val=\"24\"/></w:rPr><w:t>")
                .append(esc(texto)).append("</w:t></w:r></w:p>");
    }

    private static void parrafo(StringBuilder xml, String texto) {
        xml.append("<w:p><w:r><w:t>").append(esc(texto)).append("</w:t></w:r></w:p>");
    }

    private static void tablaDesdeConfiguracion(
            StringBuilder xml,
            Properties configuracion,
            String prefijo,
            String[] encabezados
    ) {
        int filas = Integer.parseInt(configuracion.getProperty(prefijo + ".filas", "0"));
        String[][] datos = new String[filas + 1][encabezados.length];
        datos[0] = encabezados;
        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < encabezados.length; columna++) {
                datos[fila + 1][columna] = configuracion.getProperty(prefijo + "." + fila + "." + columna, "");
            }
        }
        tabla(xml, datos);
    }

    private static void tablaUtilizacion(StringBuilder xml, ResumenSimulacion resumen) {
        String[][] datos = new String[resumen.getAtendidosPorSurtidor().size() + 1][3];
        datos[0] = new String[]{"Surtidor", "Atendidos", "Utilizacion"};
        int fila = 1;
        for (String surtidor : resumen.getAtendidosPorSurtidor().keySet()) {
            datos[fila][0] = surtidor;
            datos[fila][1] = String.valueOf(resumen.getAtendidosPorSurtidor().get(surtidor));
            datos[fila][2] = f(resumen.getUtilizacionPorSurtidor().get(surtidor)) + "%";
            fila++;
        }
        tabla(xml, datos);
    }

    private static void tablaResultados(StringBuilder xml, List<ResultadoVehiculo> resultados) {
        int limite = Math.min(100, resultados.size());
        String[][] datos = new String[limite + 1][ExportadorResultados.ENCABEZADOS.length];
        datos[0] = ExportadorResultados.ENCABEZADOS;
        for (int i = 0; i < limite; i++) {
            datos[i + 1] = ExportadorResultados.fila(resultados.get(i));
        }
        tabla(xml, datos);
    }

    private static void tabla(StringBuilder xml, String[][] datos) {
        xml.append("<w:tbl><w:tblPr><w:tblBorders>");
        xml.append("<w:top w:val=\"single\" w:sz=\"4\"/><w:left w:val=\"single\" w:sz=\"4\"/>");
        xml.append("<w:bottom w:val=\"single\" w:sz=\"4\"/><w:right w:val=\"single\" w:sz=\"4\"/>");
        xml.append("<w:insideH w:val=\"single\" w:sz=\"4\"/><w:insideV w:val=\"single\" w:sz=\"4\"/>");
        xml.append("</w:tblBorders></w:tblPr>");
        for (String[] fila : datos) {
            xml.append("<w:tr>");
            for (String celda : fila) {
                xml.append("<w:tc><w:p><w:r><w:t>")
                        .append(esc(celda == null ? "" : celda))
                        .append("</w:t></w:r></w:p></w:tc>");
            }
            xml.append("</w:tr>");
        }
        xml.append("</w:tbl>");
    }

    private static String f(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private static String esc(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
