// Simulador.java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Simulador {

    public static final double INVENTARIO_INICIAL_DEFECTO = 3000.0;
    public static final double CAPACIDAD_MAXIMA_DEFECTO = 30000.0;
    public static final double NIVEL_MINIMO_DEFECTO = 3000.0;
    public static final double TIEMPO_CISTERNA_MIN_DEFECTO = 10.0;
    public static final double TIEMPO_CISTERNA_MAX_DEFECTO = 20.0;
    public static final double CARGA_CISTERNA_MIN_DEFECTO = 15000.0;
    public static final double CARGA_CISTERNA_MAX_DEFECTO = 25000.0;
    public static final double DESCARGA_MIN_DEFECTO = 5.0;
    public static final double DESCARGA_MAX_DEFECTO = 10.0;

    private final int n;
    private final double lambda;
    private final double precioInicial;
    private final double incrementoTrimestral;
    private final double precioInternacional;
    private final int trimestre;
    private final Random random;

    private final double inventarioInicial;
    private final double capacidadMaxima;
    private final double nivelMinimo;
    private final double tiempoCisternaMinimo;
    private final double tiempoCisternaMaximo;
    private final double cargaCisternaMinima;
    private final double cargaCisternaMaxima;
    private final double descargaMinima;
    private final double descargaMaxima;

    private double inventarioActual;
    private double proximaLlegadaCisterna;
    private double proximoFinDescarga;
    private double cargaCisternaPendiente;
    private boolean cisternaProgramada;
    private int cisternasRecibidas;
    private double litrosAbastecidosReales;
    private double litrosVendidosSubvencionados;
    private double litrosVendidosInternacionales;
    private double ingresoSubvencionado;
    private double ingresoInternacional;
    private double litrosNoVendidos;
    private double perdidaEstimada;

    private final List<PerfilVehiculo> perfiles;
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
        this(
                n,
                lambda,
                precioInicial,
                incrementoTrimestral,
                precioInternacional,
                trimestre,
                semilla,
                INVENTARIO_INICIAL_DEFECTO,
                CAPACIDAD_MAXIMA_DEFECTO,
                NIVEL_MINIMO_DEFECTO,
                TIEMPO_CISTERNA_MIN_DEFECTO,
                TIEMPO_CISTERNA_MAX_DEFECTO,
                CARGA_CISTERNA_MIN_DEFECTO,
                CARGA_CISTERNA_MAX_DEFECTO,
                DESCARGA_MIN_DEFECTO,
                DESCARGA_MAX_DEFECTO
        );
    }

    public Simulador(
            int n,
            double lambda,
            double precioInicial,
            double incrementoTrimestral,
            double precioInternacional,
            int trimestre,
            long semilla,
            double inventarioInicial,
            double capacidadMaxima,
            double nivelMinimo,
            double tiempoCisternaMinimo,
            double tiempoCisternaMaximo,
            double cargaCisternaMinima,
            double cargaCisternaMaxima,
            double descargaMinima,
            double descargaMaxima
    ) {
        this.n = n;
        this.lambda = lambda;
        this.precioInicial = precioInicial;
        this.incrementoTrimestral = incrementoTrimestral;
        this.precioInternacional = precioInternacional;
        this.trimestre = trimestre;
        this.random = new Random(semilla);

        this.capacidadMaxima = Math.max(1.0, capacidadMaxima);
        this.inventarioInicial = limitar(inventarioInicial, 0.0, this.capacidadMaxima);
        this.inventarioActual = this.inventarioInicial;
        this.nivelMinimo = limitar(nivelMinimo, 0.0, this.capacidadMaxima);
        this.tiempoCisternaMinimo = Math.max(0.0, tiempoCisternaMinimo);
        this.tiempoCisternaMaximo = Math.max(this.tiempoCisternaMinimo, tiempoCisternaMaximo);
        this.cargaCisternaMinima = Math.max(0.0, cargaCisternaMinima);
        this.cargaCisternaMaxima = Math.max(this.cargaCisternaMinima, cargaCisternaMaxima);
        this.descargaMinima = Math.max(0.0, descargaMinima);
        this.descargaMaxima = Math.max(this.descargaMinima, descargaMaxima);

        this.cisternasRecibidas = 0;
        this.litrosAbastecidosReales = 0.0;
        this.litrosVendidosSubvencionados = 0.0;
        this.litrosVendidosInternacionales = 0.0;
        this.ingresoSubvencionado = 0.0;
        this.ingresoInternacional = 0.0;
        this.litrosNoVendidos = 0.0;
        this.perdidaEstimada = 0.0;
        this.cisternaProgramada = false;

        this.perfiles = crearPerfilesPorDefecto();

        this.surtidoresSubvencionados = new ArrayList<>();
        this.surtidoresSubvencionados.add(new Surtidor("S1", "Subvencionado"));
        this.surtidoresSubvencionados.add(new Surtidor("S2", "Subvencionado"));
        this.surtidoresSubvencionados.add(new Surtidor("S3", "Subvencionado"));
        this.surtidoresSubvencionados.add(new Surtidor("S4", "Subvencionado"));

        this.surtidorInternacional = new Surtidor("S5", "Internacional");
        asegurarCisternaProgramada(0.0);
    }

    public List<ResultadoVehiculo> ejecutar() {
        List<ResultadoVehiculo> resultados = new ArrayList<>();

        double horaLlegada = 0.0;
        double precioSubvencionado = calcularPrecioSubvencionado();

        for (int i = 1; i <= n; i++) {
            double rLlegada = Math.max(random.nextDouble(), 0.000001);
            double tiempoEntreLlegadas = -Math.log(rLlegada) / lambda;
            horaLlegada += tiempoEntreLlegadas;

            procesarCisternasHasta(horaLlegada);

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
            double precioAplicado;

            if (costoSub <= costoInt) {
                surtidorAsignado = surtidorSubMasLibre;
                decision = "Subvencionado";
                costoFinal = costoSub;
                precioAplicado = precioSubvencionado;
            } else {
                surtidorAsignado = surtidorInternacional;
                decision = "Internacional";
                costoFinal = costoInt;
                precioAplicado = precioInternacional;
            }

            double inventarioAntes = inventarioActual;

            if (inventarioActual >= vehiculo.getLitros()) {
                double inicio = Math.max(
                        vehiculo.getHoraLlegada(),
                        surtidorAsignado.getTiempoLibre()
                );

                double fin = inicio + vehiculo.getTiempoServicio();
                double esperaReal = inicio - vehiculo.getHoraLlegada();
                double tiempoTotal = fin - vehiculo.getHoraLlegada();
                double ingreso = precioAplicado * vehiculo.getLitros();

                inventarioActual -= vehiculo.getLitros();
                surtidorAsignado.registrarAtencion(inicio, fin);
                acumularVenta(decision, vehiculo.getLitros(), ingreso);

                resultados.add(new ResultadoVehiculo(
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
                        costoFinal,
                        "Atendido",
                        "",
                        precioAplicado,
                        ingreso,
                        0.0,
                        inventarioAntes,
                        inventarioActual,
                        vehiculo.getLitros(),
                        0.0
                ));
                asegurarCisternaProgramada(vehiculo.getHoraLlegada());
            } else {
                double perdida = precioAplicado * vehiculo.getLitros();
                litrosNoVendidos += vehiculo.getLitros();
                perdidaEstimada += perdida;

                resultados.add(new ResultadoVehiculo(
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
                        "-",
                        vehiculo.getHoraLlegada(),
                        vehiculo.getHoraLlegada(),
                        0.0,
                        0.0,
                        costoFinal,
                        "No atendido",
                        "Falta de stock",
                        precioAplicado,
                        0.0,
                        perdida,
                        inventarioAntes,
                        inventarioActual,
                        0.0,
                        vehiculo.getLitros()
                ));
                asegurarCisternaProgramada(vehiculo.getHoraLlegada());
            }
        }

        return resultados;
    }

    private List<PerfilVehiculo> crearPerfilesPorDefecto() {
        List<PerfilVehiculo> perfilesDefecto = new ArrayList<>();
        perfilesDefecto.add(new PerfilVehiculo("Particular", 0.45, "Gasolina", "GNV", 0.70, 20, 45, 3, 6, 30));
        perfilesDefecto.add(new PerfilVehiculo("Publico", 0.75, "GNV", "Gasolina", 0.80, 25, 60, 4, 7, 60));
        perfilesDefecto.add(new PerfilVehiculo("Interprovincial", 0.90, "Diesel", "Gasolina", 0.75, 60, 150, 5, 9, 100));
        perfilesDefecto.add(new PerfilVehiculo("Pesado", 1.00, "Diesel", "Diesel", 1.00, 200, 400, 7, 12, 150));
        return perfilesDefecto;
    }

    public void configurarPerfil(
            String tipo,
            double litrosMinimos,
            double litrosMaximos,
            double servicioMinimo,
            double servicioMaximo,
            double costoOportunidad
    ) {
        for (PerfilVehiculo perfil : perfiles) {
            if (perfil.getTipo().equalsIgnoreCase(tipo)) {
                perfil.configurarRangos(
                        litrosMinimos,
                        litrosMaximos,
                        servicioMinimo,
                        servicioMaximo,
                        costoOportunidad
                );
                return;
            }
        }
    }

    private Vehiculo generarVehiculo(int id, double horaLlegada) {
        double rTipo = random.nextDouble();
        PerfilVehiculo perfil = perfiles.get(perfiles.size() - 1);

        for (PerfilVehiculo candidato : perfiles) {
            if (rTipo < candidato.getProbabilidadAcumulada()) {
                perfil = candidato;
                break;
            }
        }

        String carburante = random.nextDouble() < perfil.getProbabilidadPrincipal()
                ? perfil.getCarburantePrincipal()
                : perfil.getCarburanteSecundario();

        return new Vehiculo(
                id,
                perfil.getTipo(),
                carburante,
                aleatorioEntre(perfil.getLitrosMinimos(), perfil.getLitrosMaximos()),
                perfil.getCostoOportunidad(),
                horaLlegada,
                aleatorioEntre(perfil.getServicioMinimo(), perfil.getServicioMaximo())
        );
    }

    private void procesarCisternasHasta(double tiempoActual) {
        while (cisternaProgramada && tiempoActual >= proximoFinDescarga) {
            recibirCisterna();
            cisternaProgramada = false;
            if (inventarioActual <= nivelMinimo) {
                programarSiguienteCisterna(proximoFinDescarga);
            } else {
                break;
            }
        }
    }

    private void asegurarCisternaProgramada(double desde) {
        if (!cisternaProgramada && inventarioActual <= nivelMinimo) {
            programarSiguienteCisterna(desde);
        }
    }

    private void programarSiguienteCisterna(double desde) {
        this.proximaLlegadaCisterna = desde + aleatorioEntre(tiempoCisternaMinimo, tiempoCisternaMaximo);
        this.cargaCisternaPendiente = aleatorioEntre(cargaCisternaMinima, cargaCisternaMaxima);
        this.proximoFinDescarga = proximaLlegadaCisterna + aleatorioEntre(descargaMinima, descargaMaxima);
        if (this.proximoFinDescarga <= desde) {
            this.proximoFinDescarga = desde + 0.000001;
        }
        this.cisternaProgramada = true;
    }

    private void recibirCisterna() {
        if (inventarioActual >= capacidadMaxima) {
            return;
        }

        double espacioDisponible = capacidadMaxima - inventarioActual;
        double litrosRecibidos = Math.min(cargaCisternaPendiente, espacioDisponible);

        inventarioActual += litrosRecibidos;
        cisternasRecibidas++;
        litrosAbastecidosReales += litrosRecibidos;
    }

    private void acumularVenta(String decision, double litros, double ingreso) {
        if (decision.equals("Subvencionado")) {
            litrosVendidosSubvencionados += litros;
            ingresoSubvencionado += ingreso;
        } else {
            litrosVendidosInternacionales += litros;
            ingresoInternacional += ingreso;
        }
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
        if (maximo <= minimo) {
            return minimo;
        }
        return minimo + (maximo - minimo) * random.nextDouble();
    }

    private double limitar(double valor, double minimo, double maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    public void imprimirTabla(List<ResultadoVehiculo> resultados, int limite) {
        System.out.println();
        System.out.println("Precio subvencionado del trimestre " + trimestre + ": Bs. "
                + String.format(Locale.US, "%.2f", calcularPrecioSubvencionado()) + "/L");
        System.out.println("Precio internacional: Bs. "
                + String.format(Locale.US, "%.2f", precioInternacional) + "/L");

        System.out.println();
        System.out.println("TABLA INDIVIDUAL RESUMIDA");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf(
                "%-4s %-7s %-13s %-18s %-10s %8s %-15s %-4s %8s %8s %10s %10s %-12s%n",
                "ID", "HL", "Estado", "Tipo", "Carb.", "Litros", "Decision", "S",
                "Vend.", "NoVend.", "Ingreso", "Perdida", "Motivo"
        );
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < limite; i++) {
            System.out.println(resultados.get(i).filaTabla());
        }

        System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
    }

    public void imprimirResumen(List<ResultadoVehiculo> resultados) {
        ResumenSimulacion resumen = ResumenSimulacion.desde(resultados, this);

        System.out.println();
        System.out.println("RESUMEN OPERATIVO");
        System.out.println("-------------------------------------------------");
        System.out.printf(Locale.US, "Vehiculos simulados: %d%n", resumen.getVehiculosSimulados());
        System.out.printf(Locale.US, "Vehiculos atendidos: %d%n", resumen.getVehiculosAtendidos());
        System.out.printf(Locale.US, "Vehiculos no atendidos: %d%n", resumen.getVehiculosNoAtendidos());
        System.out.printf(Locale.US, "Ruta subvencionada: %d vehiculos (%.2f%%)%n",
                resumen.getCantidadSubvencionada(), resumen.getPorcentajeSubvencionado());
        System.out.printf(Locale.US, "Ruta internacional: %d vehiculos (%.2f%%)%n",
                resumen.getCantidadInternacional(), resumen.getPorcentajeInternacional());
        System.out.printf(Locale.US, "Espera promedio atendidos: %.2f min%n", resumen.getEsperaPromedio());
        System.out.printf(Locale.US, "Servicio promedio atendidos: %.2f min%n", resumen.getServicioPromedio());
        System.out.printf(Locale.US, "Tiempo total promedio atendidos: %.2f min%n", resumen.getTiempoTotalPromedio());
        System.out.printf(Locale.US, "Horizonte simulado: %.2f min%n", resumen.getHorizonteSimulado());

        System.out.println();
        System.out.println("RESUMEN ECONOMICO");
        System.out.println("-------------------------------------------------");
        System.out.printf(Locale.US, "Litros vendidos subvencionados: %.2f L%n", resumen.getLitrosVendidosSubvencionados());
        System.out.printf(Locale.US, "Litros vendidos internacionales: %.2f L%n", resumen.getLitrosVendidosInternacionales());
        System.out.printf(Locale.US, "Litros vendidos totales: %.2f L%n", resumen.getLitrosVendidosTotales());
        System.out.printf(Locale.US, "Ingreso subvencionado: Bs. %.2f%n", resumen.getIngresoSubvencionado());
        System.out.printf(Locale.US, "Ingreso internacional: Bs. %.2f%n", resumen.getIngresoInternacional());
        System.out.printf(Locale.US, "Ingreso total: Bs. %.2f%n", resumen.getIngresoTotal());
        System.out.printf(Locale.US, "Litros no vendidos: %.2f L%n", resumen.getLitrosNoVendidos());
        System.out.printf(Locale.US, "Perdida estimada: Bs. %.2f%n", resumen.getPerdidaEstimada());

        System.out.println();
        System.out.println("RESUMEN DE ABASTECIMIENTO");
        System.out.println("-------------------------------------------------");
        System.out.printf(Locale.US, "Inventario inicial: %.2f L%n", resumen.getInventarioInicial());
        System.out.printf(Locale.US, "Inventario final: %.2f L%n", resumen.getInventarioFinal());
        System.out.printf(Locale.US, "Capacidad maxima: %.2f L%n", capacidadMaxima);
        System.out.printf(Locale.US, "Nivel minimo: %.2f L%n", nivelMinimo);
        System.out.printf(Locale.US, "Cisternas recibidas: %d%n", resumen.getCisternasRecibidas());
        System.out.printf(Locale.US, "Litros abastecidos reales: %.2f L%n", resumen.getLitrosAbastecidosReales());

        System.out.println();
        System.out.println("UTILIZACION DE SURTIDORES");
        System.out.println("-------------------------------------------------");

        for (Surtidor s : surtidoresSubvencionados) {
            double utilizacion = resumen.getUtilizacionPorSurtidor().get(s.getNombre());
            System.out.printf(Locale.US, "%s %-15s | Atendidos: %3d | Utilizacion: %6.2f%%%n",
                    s.getNombre(),
                    s.getTipoRuta(),
                    s.getVehiculosAtendidos(),
                    utilizacion);
        }

        double utilizacionInt = resumen.getUtilizacionPorSurtidor().get(surtidorInternacional.getNombre());
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

    public double getInventarioInicial() {
        return inventarioInicial;
    }

    public double getInventarioActual() {
        return inventarioActual;
    }

    public double getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public double getNivelMinimo() {
        return nivelMinimo;
    }

    public int getCisternasRecibidas() {
        return cisternasRecibidas;
    }

    public double getLitrosAbastecidosReales() {
        return litrosAbastecidosReales;
    }

    public double getLitrosVendidosSubvencionados() {
        return litrosVendidosSubvencionados;
    }

    public double getLitrosVendidosInternacionales() {
        return litrosVendidosInternacionales;
    }

    public double getLitrosVendidosTotales() {
        return litrosVendidosSubvencionados + litrosVendidosInternacionales;
    }

    public double getIngresoSubvencionado() {
        return ingresoSubvencionado;
    }

    public double getIngresoInternacional() {
        return ingresoInternacional;
    }

    public double getIngresoTotal() {
        return ingresoSubvencionado + ingresoInternacional;
    }

    public double getLitrosNoVendidos() {
        return litrosNoVendidos;
    }

    public double getPerdidaEstimada() {
        return perdidaEstimada;
    }
}
