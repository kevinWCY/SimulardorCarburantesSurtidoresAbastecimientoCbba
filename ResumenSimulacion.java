// ResumenSimulacion.java
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResumenSimulacion {

    private final int totalVehiculos;
    private final int vehiculosSimulados;
    private final long cantidadSubvencionada;
    private final long cantidadInternacional;
    private final long vehiculosAtendidos;
    private final long vehiculosNoAtendidos;
    private final double porcentajeSubvencionado;
    private final double porcentajeInternacional;
    private final double esperaPromedio;
    private final double servicioPromedio;
    private final double tiempoTotalPromedio;
    private final double costoPromedio;
    private final double horizonteSimulado;
    private final double litrosVendidosSubvencionados;
    private final double litrosVendidosInternacionales;
    private final double litrosVendidosTotales;
    private final double ingresoSubvencionado;
    private final double ingresoInternacional;
    private final double ingresoTotal;
    private final double inventarioInicial;
    private final double inventarioFinal;
    private final int cisternasRecibidas;
    private final double litrosAbastecidosReales;
    private final double litrosNoVendidos;
    private final double perdidaEstimada;

    private final Map<String, Integer> atendidosPorSurtidor;
    private final Map<String, Double> utilizacionPorSurtidor;

    public ResumenSimulacion(
            int totalVehiculos,
            int vehiculosSimulados,
            long cantidadSubvencionada,
            long cantidadInternacional,
            long vehiculosAtendidos,
            long vehiculosNoAtendidos,
            double porcentajeSubvencionado,
            double porcentajeInternacional,
            double esperaPromedio,
            double servicioPromedio,
            double tiempoTotalPromedio,
            double costoPromedio,
            double horizonteSimulado,
            double litrosVendidosSubvencionados,
            double litrosVendidosInternacionales,
            double litrosVendidosTotales,
            double ingresoSubvencionado,
            double ingresoInternacional,
            double ingresoTotal,
            double inventarioInicial,
            double inventarioFinal,
            int cisternasRecibidas,
            double litrosAbastecidosReales,
            double litrosNoVendidos,
            double perdidaEstimada,
            Map<String, Integer> atendidosPorSurtidor,
            Map<String, Double> utilizacionPorSurtidor
    ) {
        this.totalVehiculos = totalVehiculos;
        this.vehiculosSimulados = vehiculosSimulados;
        this.cantidadSubvencionada = cantidadSubvencionada;
        this.cantidadInternacional = cantidadInternacional;
        this.vehiculosAtendidos = vehiculosAtendidos;
        this.vehiculosNoAtendidos = vehiculosNoAtendidos;
        this.porcentajeSubvencionado = porcentajeSubvencionado;
        this.porcentajeInternacional = porcentajeInternacional;
        this.esperaPromedio = esperaPromedio;
        this.servicioPromedio = servicioPromedio;
        this.tiempoTotalPromedio = tiempoTotalPromedio;
        this.costoPromedio = costoPromedio;
        this.horizonteSimulado = horizonteSimulado;
        this.litrosVendidosSubvencionados = litrosVendidosSubvencionados;
        this.litrosVendidosInternacionales = litrosVendidosInternacionales;
        this.litrosVendidosTotales = litrosVendidosTotales;
        this.ingresoSubvencionado = ingresoSubvencionado;
        this.ingresoInternacional = ingresoInternacional;
        this.ingresoTotal = ingresoTotal;
        this.inventarioInicial = inventarioInicial;
        this.inventarioFinal = inventarioFinal;
        this.cisternasRecibidas = cisternasRecibidas;
        this.litrosAbastecidosReales = litrosAbastecidosReales;
        this.litrosNoVendidos = litrosNoVendidos;
        this.perdidaEstimada = perdidaEstimada;
        this.atendidosPorSurtidor = atendidosPorSurtidor;
        this.utilizacionPorSurtidor = utilizacionPorSurtidor;
    }

    public static ResumenSimulacion desde(List<ResultadoVehiculo> resultados, Simulador simulador) {
        int total = resultados.size();

        long atendidos = resultados.stream()
                .filter(r -> r.getEstado().equals("Atendido"))
                .count();

        long noAtendidos = total - atendidos;

        long sub = resultados.stream()
                .filter(r -> r.getEstado().equals("Atendido"))
                .filter(r -> r.getDecision().equals("Subvencionado"))
                .count();

        long intl = resultados.stream()
                .filter(r -> r.getEstado().equals("Atendido"))
                .filter(r -> r.getDecision().equals("Internacional"))
                .count();

        double esperaProm = resultados.stream()
                .filter(r -> r.getEstado().equals("Atendido"))
                .mapToDouble(ResultadoVehiculo::getEsperaReal)
                .average()
                .orElse(0.0);

        double servicioProm = resultados.stream()
                .filter(r -> r.getEstado().equals("Atendido"))
                .mapToDouble(ResultadoVehiculo::getTiempoServicio)
                .average()
                .orElse(0.0);

        double tiempoTotalProm = resultados.stream()
                .filter(r -> r.getEstado().equals("Atendido"))
                .mapToDouble(ResultadoVehiculo::getTiempoTotal)
                .average()
                .orElse(0.0);

        double costoProm = resultados.stream()
                .filter(r -> r.getEstado().equals("Atendido"))
                .mapToDouble(ResultadoVehiculo::getCostoFinal)
                .average()
                .orElse(0.0);

        double horizonte = resultados.stream()
                .mapToDouble(r -> Math.max(r.getHoraLlegada(), r.getFin()))
                .max()
                .orElse(1.0);

        if (horizonte <= 0) {
            horizonte = 1.0;
        }

        Map<String, Integer> atendidosPorSurtidor = new LinkedHashMap<>();
        Map<String, Double> utilizaciones = new LinkedHashMap<>();

        for (Surtidor s : simulador.getSurtidoresSubvencionados()) {
            atendidosPorSurtidor.put(s.getNombre(), s.getVehiculosAtendidos());
            utilizaciones.put(s.getNombre(), (s.getTiempoOcupado() / horizonte) * 100.0);
        }

        Surtidor sint = simulador.getSurtidorInternacional();
        atendidosPorSurtidor.put(sint.getNombre(), sint.getVehiculosAtendidos());
        utilizaciones.put(sint.getNombre(), (sint.getTiempoOcupado() / horizonte) * 100.0);

        double porcentajeSub = total > 0 ? sub * 100.0 / total : 0.0;
        double porcentajeInt = total > 0 ? intl * 100.0 / total : 0.0;

        return new ResumenSimulacion(
                total,
                total,
                sub,
                intl,
                atendidos,
                noAtendidos,
                porcentajeSub,
                porcentajeInt,
                esperaProm,
                servicioProm,
                tiempoTotalProm,
                costoProm,
                horizonte,
                simulador.getLitrosVendidosSubvencionados(),
                simulador.getLitrosVendidosInternacionales(),
                simulador.getLitrosVendidosTotales(),
                simulador.getIngresoSubvencionado(),
                simulador.getIngresoInternacional(),
                simulador.getIngresoTotal(),
                simulador.getInventarioInicial(),
                simulador.getInventarioActual(),
                simulador.getCisternasRecibidas(),
                simulador.getLitrosAbastecidosReales(),
                simulador.getLitrosNoVendidos(),
                simulador.getPerdidaEstimada(),
                atendidosPorSurtidor,
                utilizaciones
        );
    }

    public int getTotalVehiculos() {
        return totalVehiculos;
    }

    public int getVehiculosSimulados() {
        return vehiculosSimulados;
    }

    public long getCantidadSubvencionada() {
        return cantidadSubvencionada;
    }

    public long getCantidadInternacional() {
        return cantidadInternacional;
    }

    public long getVehiculosAtendidos() {
        return vehiculosAtendidos;
    }

    public long getVehiculosNoAtendidos() {
        return vehiculosNoAtendidos;
    }

    public double getPorcentajeSubvencionado() {
        return porcentajeSubvencionado;
    }

    public double getPorcentajeInternacional() {
        return porcentajeInternacional;
    }

    public double getEsperaPromedio() {
        return esperaPromedio;
    }

    public double getServicioPromedio() {
        return servicioPromedio;
    }

    public double getTiempoTotalPromedio() {
        return tiempoTotalPromedio;
    }

    public double getCostoPromedio() {
        return costoPromedio;
    }

    public double getHorizonteSimulado() {
        return horizonteSimulado;
    }

    public double getLitrosVendidosSubvencionados() {
        return litrosVendidosSubvencionados;
    }

    public double getLitrosVendidosInternacionales() {
        return litrosVendidosInternacionales;
    }

    public double getLitrosVendidosTotales() {
        return litrosVendidosTotales;
    }

    public double getIngresoSubvencionado() {
        return ingresoSubvencionado;
    }

    public double getIngresoInternacional() {
        return ingresoInternacional;
    }

    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public double getInventarioInicial() {
        return inventarioInicial;
    }

    public double getInventarioFinal() {
        return inventarioFinal;
    }

    public int getCisternasRecibidas() {
        return cisternasRecibidas;
    }

    public double getLitrosAbastecidosReales() {
        return litrosAbastecidosReales;
    }

    public double getLitrosNoVendidos() {
        return litrosNoVendidos;
    }

    public double getPerdidaEstimada() {
        return perdidaEstimada;
    }

    public Map<String, Integer> getAtendidosPorSurtidor() {
        return atendidosPorSurtidor;
    }

    public Map<String, Double> getUtilizacionPorSurtidor() {
        return utilizacionPorSurtidor;
    }
}
