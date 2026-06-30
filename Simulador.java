// Simulador.java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Simulador {

    private final int n;
    private final double lambda;
    private final double precioInicial;
    private final double incrementoTrimestral;
    private final double precioInternacional;
    private final int trimestre;
    private final Random random;

    private final List<Surtidor> surtidoresSubvencionados;
    private final Surtidor surtidorInternacional;

    public Simulador(
            int n,
            double lambda,
            double precioInicial,
            double incrementoTrimestral,
            double precioInternacional,
            int trimestre,
            long semilla
    ) {
        this.n = n;
        this.lambda = lambda;
        this.precioInicial = precioInicial;
        this.incrementoTrimestral = incrementoTrimestral;
        this.precioInternacional = precioInternacional;
        this.trimestre = trimestre;
        this.random = new Random(semilla);

        this.surtidoresSubvencionados = new ArrayList<>();
        this.surtidoresSubvencionados.add(new Surtidor("S1", "Subvencionado"));
        this.surtidoresSubvencionados.add(new Surtidor("S2", "Subvencionado"));
        this.surtidoresSubvencionados.add(new Surtidor("S3", "Subvencionado"));
        this.surtidoresSubvencionados.add(new Surtidor("S4", "Subvencionado"));

        this.surtidorInternacional = new Surtidor("S5", "Internacional");
    }

    public List<ResultadoVehiculo> ejecutar() {
        List<ResultadoVehiculo> resultados = new ArrayList<>();

        double horaLlegada = 0.0;
        double precioSubvencionado = calcularPrecioSubvencionado();

        for (int i = 1; i <= n; i++) {
            double rLlegada = Math.max(random.nextDouble(), 0.000001);
            double tiempoEntreLlegadas = -Math.log(rLlegada) / lambda;
            horaLlegada += tiempoEntreLlegadas;

            Vehiculo vehiculo = generarVehiculo(i, horaLlegada);

            Surtidor surtidorSubMasLibre = obtenerSurtidorSubvencionadoMasLibre();

            double esperaSub = surtidorSubMasLibre.calcularEspera(vehiculo.getHoraLlegada());
            double esperaInt = surtidorInternacional.calcularEspera(vehiculo.getHoraLlegada());

            double costoSub = calcularCosto(
                    precioSubvencionado,
                    vehiculo.getLitros(),
                    esperaSub,
                    vehiculo.getCostoOportunidad()
            );

            double costoInt = calcularCosto(
                    precioInternacional,
                    vehiculo.getLitros(),
                    esperaInt,
                    vehiculo.getCostoOportunidad()
            );

            Surtidor surtidorAsignado;
            String decision;
            double costoFinal;

            if (costoSub <= costoInt) {
                surtidorAsignado = surtidorSubMasLibre;
                decision = "Subvencionado";
                costoFinal = costoSub;
            } else {
                surtidorAsignado = surtidorInternacional;
                decision = "Internacional";
                costoFinal = costoInt;
            }

            double inicio = Math.max(
                    vehiculo.getHoraLlegada(),
                    surtidorAsignado.getTiempoLibre()
            );

            double fin = inicio + vehiculo.getTiempoServicio();
            double esperaReal = inicio - vehiculo.getHoraLlegada();
            double tiempoTotal = fin - vehiculo.getHoraLlegada();

            surtidorAsignado.registrarAtencion(inicio, fin);

            ResultadoVehiculo resultado = new ResultadoVehiculo(
                    vehiculo.getId(),
                    vehiculo.getHoraLlegada(),
                    vehiculo.getTipoVehiculo(),
                    vehiculo.getCarburante(),
                    vehiculo.getLitros(),
                    vehiculo.getTiempoServicio(),
                    esperaSub,
                    esperaInt,
                    costoSub,
                    costoInt,
                    decision,
                    surtidorAsignado.getNombre(),
                    inicio,
                    fin,
                    esperaReal,
                    tiempoTotal,
                    costoFinal
            );

            resultados.add(resultado);
        }

        return resultados;
    }

    private Vehiculo generarVehiculo(int id, double horaLlegada) {
        double rTipo = random.nextDouble();

        String tipo;
        String carburante;
        double litros;
        double costoOportunidad;
        double tiempoServicio;

        if (rTipo < 0.45) {
            tipo = "Particular";
            carburante = random.nextDouble() < 0.70 ? "Gasolina" : "GNV";
            litros = aleatorioEntre(20, 45);
            costoOportunidad = 30;
            tiempoServicio = aleatorioEntre(3, 6);
        } else if (rTipo < 0.75) {
            tipo = "Publico";
            carburante = random.nextDouble() < 0.80 ? "GNV" : "Gasolina";
            litros = aleatorioEntre(25, 60);
            costoOportunidad = 60;
            tiempoServicio = aleatorioEntre(4, 7);
        } else if (rTipo < 0.90) {
            tipo = "Interprovincial";
            carburante = random.nextDouble() < 0.75 ? "Diesel" : "Gasolina";
            litros = aleatorioEntre(60, 150);
            costoOportunidad = 100;
            tiempoServicio = aleatorioEntre(5, 9);
        } else {
            tipo = "Pesado";
            carburante = "Diesel";
            litros = aleatorioEntre(200, 400);
            costoOportunidad = 150;
            tiempoServicio = aleatorioEntre(7, 12);
        }

        return new Vehiculo(
                id,
                tipo,
                carburante,
                litros,
                costoOportunidad,
                horaLlegada,
                tiempoServicio
        );
    }

    private double calcularPrecioSubvencionado() {
        return Math.min(
                precioInicial + incrementoTrimestral * trimestre,
                precioInternacional
        );
    }

    private double calcularCosto(
            double precio,
            double litros,
            double esperaMinutos,
            double costoOportunidad
    ) {
        return precio * litros + (esperaMinutos / 60.0) * costoOportunidad;
    }

    private Surtidor obtenerSurtidorSubvencionadoMasLibre() {
        return surtidoresSubvencionados
                .stream()
                .min(Comparator.comparingDouble(Surtidor::getTiempoLibre))
                .orElse(surtidoresSubvencionados.get(0));
    }

    private double aleatorioEntre(double minimo, double maximo) {
        return minimo + (maximo - minimo) * random.nextDouble();
    }

    public void imprimirTabla(List<ResultadoVehiculo> resultados, int limite) {
        System.out.println();
        System.out.println("Precio subvencionado del trimestre " + trimestre + ": Bs. "
                + String.format(Locale.US, "%.2f", calcularPrecioSubvencionado()) + "/L");
        System.out.println("Precio internacional: Bs. "
                + String.format(Locale.US, "%.2f", precioInternacional) + "/L");

        System.out.println();
        System.out.println("PRIMEROS VEHICULOS SIMULADOS");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(
                "%-4s %-7s %-18s %-10s %8s %7s %8s %8s %10s %10s %-15s %-4s %8s %8s %8s%n",
                "ID", "HL", "Tipo", "Carb.", "Litros", "TS", "Wsub", "Wint",
                "Csub", "Cint", "Decision", "S", "Inicio", "Fin", "WT"
        );
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < limite; i++) {
            System.out.println(resultados.get(i).filaTabla());
        }

        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    public void imprimirResumen(List<ResultadoVehiculo> resultados) {
        int total = resultados.size();

        long cantidadSub = resultados.stream()
                .filter(r -> r.getDecision().equals("Subvencionado"))
                .count();

        long cantidadInt = total - cantidadSub;

        double esperaPromedio = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getEsperaReal)
                .average()
                .orElse(0.0);

        double servicioPromedio = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getTiempoServicio)
                .average()
                .orElse(0.0);

        double tiempoTotalPromedio = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getTiempoTotal)
                .average()
                .orElse(0.0);

        double costoPromedio = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getCostoFinal)
                .average()
                .orElse(0.0);

        double horizonte = resultados.stream()
                .mapToDouble(ResultadoVehiculo::getFin)
                .max()
                .orElse(1.0);

        System.out.println();
        System.out.println("RESUMEN DE INDICADORES");
        System.out.println("-------------------------------------------------");
        System.out.printf(Locale.US, "Vehiculos simulados: %d%n", total);
        System.out.printf(Locale.US, "Ruta subvencionada: %d vehiculos (%.2f%%)%n",
                cantidadSub, (cantidadSub * 100.0 / total));
        System.out.printf(Locale.US, "Ruta internacional: %d vehiculos (%.2f%%)%n",
                cantidadInt, (cantidadInt * 100.0 / total));
        System.out.printf(Locale.US, "Espera promedio: %.2f min%n", esperaPromedio);
        System.out.printf(Locale.US, "Servicio promedio: %.2f min%n", servicioPromedio);
        System.out.printf(Locale.US, "Tiempo total promedio: %.2f min%n", tiempoTotalPromedio);
        System.out.printf(Locale.US, "Costo promedio: Bs. %.2f%n", costoPromedio);
        System.out.printf(Locale.US, "Horizonte simulado: %.2f min%n", horizonte);

        System.out.println();
        System.out.println("UTILIZACION DE SURTIDORES");
        System.out.println("-------------------------------------------------");

        for (Surtidor s : surtidoresSubvencionados) {
            double utilizacion = (s.getTiempoOcupado() / horizonte) * 100.0;
            System.out.printf(Locale.US, "%s %-15s | Atendidos: %3d | Utilizacion: %6.2f%%%n",
                    s.getNombre(),
                    s.getTipoRuta(),
                    s.getVehiculosAtendidos(),
                    utilizacion);
        }

        double utilizacionInt = (surtidorInternacional.getTiempoOcupado() / horizonte) * 100.0;
        System.out.printf(Locale.US, "%s %-15s | Atendidos: %3d | Utilizacion: %6.2f%%%n",
                surtidorInternacional.getNombre(),
                surtidorInternacional.getTipoRuta(),
                surtidorInternacional.getVehiculosAtendidos(),
                utilizacionInt);
    }

        public List<Surtidor> getSurtidoresSubvencionados() {
        return surtidoresSubvencionados;
    }

    public Surtidor getSurtidorInternacional() {
        return surtidorInternacional;
    }

    public double getPrecioSubvencionadoActual() {
        return calcularPrecioSubvencionado();
    }

    public double getPrecioInternacional() {
        return precioInternacional;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public double getLambda() {
        return lambda;
    }

    public int getN() {
        return n;
    }
    
}