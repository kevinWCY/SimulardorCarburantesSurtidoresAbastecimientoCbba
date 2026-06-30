// ResultadoVehiculo.java
import java.util.Locale;

public class ResultadoVehiculo {

    private final int id;
    private final double horaLlegada;
    private final String tipoVehiculo;
    private final String carburante;
    private final double litros;
    private final double tiempoServicio;
    private final double esperaSub;
    private final double esperaInt;
    private final double costoSub;
    private final double costoInt;
    private final String decision;
    private final String surtidor;
    private final double inicio;
    private final double fin;
    private final double esperaReal;
    private final double tiempoTotal;
    private final double costoFinal;

    public ResultadoVehiculo(
            int id,
            double horaLlegada,
            String tipoVehiculo,
            String carburante,
            double litros,
            double tiempoServicio,
            double esperaSub,
            double esperaInt,
            double costoSub,
            double costoInt,
            String decision,
            String surtidor,
            double inicio,
            double fin,
            double esperaReal,
            double tiempoTotal,
            double costoFinal
    ) {
        this.id = id;
        this.horaLlegada = horaLlegada;
        this.tipoVehiculo = tipoVehiculo;
        this.carburante = carburante;
        this.litros = litros;
        this.tiempoServicio = tiempoServicio;
        this.esperaSub = esperaSub;
        this.esperaInt = esperaInt;
        this.costoSub = costoSub;
        this.costoInt = costoInt;
        this.decision = decision;
        this.surtidor = surtidor;
        this.inicio = inicio;
        this.fin = fin;
        this.esperaReal = esperaReal;
        this.tiempoTotal = tiempoTotal;
        this.costoFinal = costoFinal;
    }

    public String filaTabla() {
        return String.format(
                Locale.US,
                "%-4d %-7.2f %-18s %-10s %8.2f %7.2f %8.2f %8.2f %10.2f %10.2f %-15s %-4s %8.2f %8.2f %8.2f",
                id,
                horaLlegada,
                tipoVehiculo,
                carburante,
                litros,
                tiempoServicio,
                esperaSub,
                esperaInt,
                costoSub,
                costoInt,
                decision,
                surtidor,
                inicio,
                fin,
                tiempoTotal
        );
    }

    public int getId() {
        return id;
    }

    public double getHoraLlegada() {
        return horaLlegada;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public String getCarburante() {
        return carburante;
    }

    public double getLitros() {
        return litros;
    }

    public double getTiempoServicio() {
        return tiempoServicio;
    }

    public double getEsperaSub() {
        return esperaSub;
    }

    public double getEsperaInt() {
        return esperaInt;
    }

    public double getCostoSub() {
        return costoSub;
    }

    public double getCostoInt() {
        return costoInt;
    }

    public String getDecision() {
        return decision;
    }

    public String getSurtidor() {
        return surtidor;
    }

    public double getInicio() {
        return inicio;
    }

    public double getFin() {
        return fin;
    }

    public double getEsperaReal() {
        return esperaReal;
    }

    public double getTiempoTotal() {
        return tiempoTotal;
    }

    public double getCostoFinal() {
        return costoFinal;
    }
}