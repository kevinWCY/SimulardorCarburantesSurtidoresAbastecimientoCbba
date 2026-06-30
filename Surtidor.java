// Surtidor.java
public class Surtidor {

    private final String nombre;
    private final String tipoRuta;
    private double tiempoLibre;
    private double tiempoOcupado;
    private int vehiculosAtendidos;

    public Surtidor(String nombre, String tipoRuta) {
        this.nombre = nombre;
        this.tipoRuta = tipoRuta;
        this.tiempoLibre = 0.0;
        this.tiempoOcupado = 0.0;
        this.vehiculosAtendidos = 0;
    }

    public double calcularEspera(double horaLlegada) {
        return Math.max(0, tiempoLibre - horaLlegada);
    }

    public void registrarAtencion(double inicio, double fin) {
        this.tiempoOcupado += (fin - inicio);
        this.tiempoLibre = fin;
        this.vehiculosAtendidos++;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoRuta() {
        return tipoRuta;
    }

    public double getTiempoLibre() {
        return tiempoLibre;
    }

    public double getTiempoOcupado() {
        return tiempoOcupado;
    }

    public int getVehiculosAtendidos() {
        return vehiculosAtendidos;
    }
}