// ExportadorResultados.java
import java.util.Locale;

public class ExportadorResultados {

    public static final String[] ENCABEZADOS = {
            "i", "HL", "tipoVehiculo", "carburante", "litros", "TS", "Wsub", "Wint",
            "Csub", "Cint", "ruta", "surtidor", "inicio", "fin", "esperaReal", "WT",
            "estado", "precioAplicado", "ingresoGenerado", "perdidaEstimada",
            "motivoNoAtendido", "inventarioAntes", "inventarioDespues"
    };

    public static String[] fila(ResultadoVehiculo r) {
        return new String[]{
                String.valueOf(r.getId()),
                f(r.getHoraLlegada()),
                r.getTipoVehiculo(),
                r.getCarburante(),
                f(r.getLitros()),
                f(r.getTiempoServicio()),
                f(r.getEsperaSub()),
                f(r.getEsperaInt()),
                f(r.getCostoSub()),
                f(r.getCostoInt()),
                r.getDecision(),
                r.getSurtidor(),
                f(r.getInicio()),
                f(r.getFin()),
                f(r.getEsperaReal()),
                f(r.getTiempoTotal()),
                r.getEstado(),
                f(r.getPrecioAplicado()),
                f(r.getIngresoGenerado()),
                f(r.getPerdidaEstimada()),
                r.getMotivoNoAtendido(),
                f(r.getInventarioAntes()),
                f(r.getInventarioDespues())
        };
    }

    public static String f(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }
}
