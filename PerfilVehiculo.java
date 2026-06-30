public class PerfilVehiculo {

    private final String tipo;
    private final double probabilidadAcumulada;
    private final String carburantePrincipal;
    private final String carburanteSecundario;
    private final double probabilidadPrincipal;
    private double litrosMinimos;
    private double litrosMaximos;
    private double servicioMinimo;
    private double servicioMaximo;
    private double costoOportunidad;

    public PerfilVehiculo(
            String tipo,
            double probabilidadAcumulada,
            String carburantePrincipal,
            String carburanteSecundario,
            double probabilidadPrincipal,
            double litrosMinimos,
            double litrosMaximos,
            double servicioMinimo,
            double servicioMaximo,
            double costoOportunidad
    ) {
        this.tipo = tipo;
        this.probabilidadAcumulada = probabilidadAcumulada;
        this.carburantePrincipal = carburantePrincipal;
        this.carburanteSecundario = carburanteSecundario;
        this.probabilidadPrincipal = probabilidadPrincipal;
        this.litrosMinimos = litrosMinimos;
        this.litrosMaximos = litrosMaximos;
        this.servicioMinimo = servicioMinimo;
        this.servicioMaximo = servicioMaximo;
        this.costoOportunidad = costoOportunidad;
    }

    public String getTipo() {
        return tipo;
    }

    public double getProbabilidadAcumulada() {
        return probabilidadAcumulada;
    }

    public String getCarburantePrincipal() {
        return carburantePrincipal;
    }

    public String getCarburanteSecundario() {
        return carburanteSecundario;
    }

    public double getProbabilidadPrincipal() {
        return probabilidadPrincipal;
    }

    public double getLitrosMinimos() {
        return litrosMinimos;
    }

    public double getLitrosMaximos() {
        return litrosMaximos;
    }

    public double getServicioMinimo() {
        return servicioMinimo;
    }

    public double getServicioMaximo() {
        return servicioMaximo;
    }

    public double getCostoOportunidad() {
        return costoOportunidad;
    }

    public void configurarRangos(
            double litrosMinimos,
            double litrosMaximos,
            double servicioMinimo,
            double servicioMaximo,
            double costoOportunidad
    ) {
        this.litrosMinimos = litrosMinimos;
        this.litrosMaximos = litrosMaximos;
        this.servicioMinimo = servicioMinimo;
        this.servicioMaximo = servicioMaximo;
        this.costoOportunidad = costoOportunidad;
    }
}
