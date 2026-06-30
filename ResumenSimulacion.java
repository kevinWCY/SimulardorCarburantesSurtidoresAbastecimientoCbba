// ResumenSimulacion.java
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResumenSimulacion {

    private final int totalVehiculos;
    private final long cantidadSubvencionada;
    private final long cantidadInternacional;
    private final double porcentajeSubvencionado;
    private final double porcentajeInternacional;
    private final double esperaPromedio;
    private final double servicioPromedio;
    private final double tiempoTotalPromedio;
    private final double costoPromedio;
    private final double horizonteSimulado;

    private final Map<String, Integer> atendidosPorSurtidor;
    private final Map<String, Double> utilizacionPorSurtidor;

    public ResumenSimulacion(
            int totalVehiculos,
            long cantidadSubvencionada,
            long cantidadInternacional,
            double porcentajeSubvencionado,
            double porcentajeInternacional,
            double esperaPromedio,
            double servicioPromedio,
            double tiempoTotalPromedio,
            double costoPromedio,
            double horizonteSimulado,
            Map<String, Integer> atendidosPorSurtidor,
            Map<String, Double> utilizacionPorSurtidor
    ) {
        this.totalVehiculos = totalVehiculos;
        this.cantidadSubvencionada = cantidadSubvencionada;
        this.cantidadInternacional = cantidadInternacional;
        this.porcentajeSubvencionado = porcentajeSubvencionado;
        this.porcentajeInternacional = porcentajeInternacional;
        this.esperaPromedio = esperaPromedio;
        this.servicioPromedio = servicioPromedio;
        this.tiempoTotalPromedio = tiempoTotalPromedio;
        this.costoPromedio = costoPromedio;
        this.horizonteSimulado = horizonteSimulado;
        this.atendidosPorSurtidor = atendidosPorSurtidor;
        this.utilizacionPorSurtidor = utilizacionPorSurtidor;
    }

    public static ResumenSimulacion desde(List<ResultadoVehiculo> resultados, Simulador simulador) {
        int total = resultados.size();

        long sub = resultados.stream()
                .filter(r -> r.getDecision().equals("Subvencionado"))
                .count();

        long intl = total - sub;

        double esperaProm = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getEsperaReal)
                .average()
                .orElse(0.0);

        double servicioProm = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getTiempoServicio)
                .average()
                .orElse(0.0);

        double tiempoTotalProm = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getTiempoTotal)
                .average()
                .orElse(0.0);

        double costoProm = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getCostoFinal)
                .average()
                .orElse(0.0);

        double horizonte = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getFin)
                .max()
                .orElse(1.0);

        if (horizonte <= 0) {
            horizonte = 1.0;
        }

        Map<String, Integer> atendidos = new LinkedHashMap<>();
        Map<String, Double> utilizaciones = new LinkedHashMap<>();

        for (Surtidor s : simulador.getSurtidoresSubvencionados()) {
            atendidos.put(s.getNombre(), s.getVehiculosAtendidos());
            utilizaciones.put(s.getNombre(), (s.getTiempoOcupado() / horizonte) * 100.0);
        }

        Surtidor sint = simulador.getSurtidorInternacional();
        atendidos.put(sint.getNombre(), sint.getVehiculosAtendidos());
        utilizaciones.put(sint.getNombre(), (sint.getTiempoOcupado() / horizonte) * 100.0);

        return new ResumenSimulacion(
                total,
                sub,
                intl,
                sub * 100.0 / total,
                intl * 100.0 / total,
                esperaProm,
                servicioProm,
                tiempoTotalProm,
                costoProm,
                horizonte,
                atendidos,
                utilizaciones
        );
    }

    public int getTotalVehiculos() {
        return totalVehiculos;
    }

    public long getCantidadSubvencionada() {
        return cantidadSubvencionada;
    }

    public long getCantidadInternacional() {
        return cantidadInternacional;
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

    public Map<String, Integer> getAtendidosPorSurtidor() {
        return atendidosPorSurtidor;
    }

    public Map<String, Double> getUtilizacionPorSurtidor() {
        return utilizacionPorSurtidor;
    }
}
