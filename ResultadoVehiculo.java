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
    private final String estado;
    private final String motivoNoAtendido;
    private final double precioAplicado;
    private final double ingresoGenerado;
    private final double perdidaEstimada;
    private final double inventarioAntes;
    private final double inventarioDespues;
    private final double litrosVendidosReales;
    private final double litrosNoVendidos;

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
            double costoFinal,
            String estado,
            String motivoNoAtendido,
            double precioAplicado,
            double ingresoGenerado,
            double perdidaEstimada,
            double inventarioAntes,
            double inventarioDespues,
            double litrosVendidosReales,
            double litrosNoVendidos
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
        this.estado = estado;
        this.motivoNoAtendido = motivoNoAtendido;
        this.precioAplicado = precioAplicado;
        this.ingresoGenerado = ingresoGenerado;
        this.perdidaEstimada = perdidaEstimada;
        this.inventarioAntes = inventarioAntes;
        this.inventarioDespues = inventarioDespues;
        this.litrosVendidosReales = litrosVendidosReales;
        this.litrosNoVendidos = litrosNoVendidos;
    }

    public String filaTabla() {
        return String.format(
                Locale.US,
                "%-4d %-7.2f %-13s %-18s %-10s %8.2f %-15s %-4s %8.2f %8.2f %10.2f %10.2f %-12s",
                id,
                horaLlegada,
                estado,
                tipoVehiculo,
                carburante,
                litros,
                decision,
                surtidor,
                litrosVendidosReales,
                litrosNoVendidos,
                ingresoGenerado,
                perdidaEstimada,
                motivoNoAtendido
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

    public String getEstado() {
        return estado;
    }

    public String getMotivoNoAtendido() {
        return motivoNoAtendido;
    }

    public double getPrecioAplicado() {
        return precioAplicado;
    }

    public double getIngresoGenerado() {
        return ingresoGenerado;
    }

    public double getPerdidaEstimada() {
        return perdidaEstimada;
    }

    public double getInventarioAntes() {
        return inventarioAntes;
    }

    public double getInventarioDespues() {
        return inventarioDespues;
    }

    public double getLitrosVendidosReales() {
        return litrosVendidosReales;
    }

    public double getLitrosNoVendidos() {
        return litrosNoVendidos;
    }
}
