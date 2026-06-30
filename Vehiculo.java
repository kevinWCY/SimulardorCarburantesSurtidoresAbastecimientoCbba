// Vehiculo.java
public class Vehiculo {

    private final int id;
    private final String tipoVehiculo;
    private final String carburante;
    private final double litros;
    private final double costoOportunidad;
    private final double horaLlegada;
    private final double tiempoServicio;

    public Vehiculo(
            int id,
            String tipoVehiculo,
            String carburante,
            double litros,
            double costoOportunidad,
            double horaLlegada,
            double tiempoServicio
    ) {
        this.id = id;
        this.tipoVehiculo = tipoVehiculo;
        this.carburante = carburante;
        this.litros = litros;
        this.costoOportunidad = costoOportunidad;
        this.horaLlegada = horaLlegada;
        this.tiempoServicio = tiempoServicio;
    }

    public int getId() {
        return id;
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

    public double getCostoOportunidad() {
        return costoOportunidad;
    }

    public double getHoraLlegada() {
        return horaLlegada;
    }

    public double getTiempoServicio() {
        return tiempoServicio;
    }
}