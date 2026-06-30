// Main.java
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        System.out.println("=================================================");
        System.out.println(" SIMULADOR DE SURTIDORES DE COMBUSTIBLES - JAVA ");
        System.out.println("=================================================");

        int n = leerEntero(sc, "Cantidad de vehiculos a simular", 50);
        double lambda = leerDecimal(sc, "Lambda llegadas vehiculos/min", 1.50);
        int trimestre = leerEntero(sc, "Trimestre a simular", 4);
        long semilla = leerLong(sc, "Semilla aleatoria", 100);

        Simulador simulador = new Simulador(
                n,
                lambda,
                6.96,
                1.32,
                12.36,
                trimestre,
                semilla,
                3000,
                30000,
                3000,
                10,
                20,
                15000,
                25000,
                5,
                10
        );

        List<ResultadoVehiculo> resultados = simulador.ejecutar();

        simulador.imprimirTabla(resultados, Math.min(n, 20));
        simulador.imprimirResumen(resultados);

        sc.close();
    }

    private static int leerEntero(Scanner sc, String mensaje, int valorDefecto) {
        System.out.print(mensaje + " [" + valorDefecto + "]: ");
        if (!sc.hasNextLine()) {
            return valorDefecto;
        }
        String entrada = sc.nextLine().trim();

        if (entrada.isEmpty()) {
            return valorDefecto;
        }

        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            System.out.println("Valor invalido. Se usara: " + valorDefecto);
            return valorDefecto;
        }
    }

    private static long leerLong(Scanner sc, String mensaje, long valorDefecto) {
        System.out.print(mensaje + " [" + valorDefecto + "]: ");
        if (!sc.hasNextLine()) {
            return valorDefecto;
        }
        String entrada = sc.nextLine().trim();

        if (entrada.isEmpty()) {
            return valorDefecto;
        }

        try {
            return Long.parseLong(entrada);
        } catch (NumberFormatException e) {
            System.out.println("Valor invalido. Se usara: " + valorDefecto);
            return valorDefecto;
        }
    }

    private static double leerDecimal(Scanner sc, String mensaje, double valorDefecto) {
        System.out.print(mensaje + " [" + valorDefecto + "]: ");
        if (!sc.hasNextLine()) {
            return valorDefecto;
        }
        String entrada = sc.nextLine().trim().replace(",", ".");

        if (entrada.isEmpty()) {
            return valorDefecto;
        }

        try {
            return Double.parseDouble(entrada);
        } catch (NumberFormatException e) {
            System.out.println("Valor invalido. Se usara: " + valorDefecto);
            return valorDefecto;
        }
    }
}
