// InterfazSimulador.java
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InterfazSimulador extends JFrame {

    private final JTextField txtN;
    private final JTextField txtLambda;
    private final JTextField txtTrimestre;
    private final JTextField txtSemilla;
    private final JTextField txtPrecioInicial;
    private final JTextField txtIncremento;
    private final JTextField txtPrecioInternacional;

    private final DefaultTableModel modeloTablaVehiculos;
    private final DefaultTableModel modeloResumen;
    private final DefaultTableModel modeloUtilizacion;

    private final GraficoResultadosPanel panelGraficos;

    public InterfazSimulador() {
        Locale.setDefault(Locale.US);

        setTitle("Simulador de Surtidores de Combustibles - Java");
        setSize(1250, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        txtN = new JTextField("500");
        txtLambda = new JTextField("1.50");
        txtTrimestre = new JTextField("4");
        txtSemilla = new JTextField("100");
        txtPrecioInicial = new JTextField("6.96");
        txtIncremento = new JTextField("1.32");
        txtPrecioInternacional = new JTextField("12.36");

        modeloTablaVehiculos = new DefaultTableModel();
        modeloResumen = new DefaultTableModel();
        modeloUtilizacion = new DefaultTableModel();

        panelGraficos = new GraficoResultadosPanel();

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Corrida de simulacion", crearPanelCorrida());
        tabs.addTab("Resultados generales", crearPanelResultados());
        tabs.addTab("Graficas", new JScrollPane(panelGraficos));

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel crearPanelCorrida() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel panelEntrada = new JPanel(new GridLayout(2, 8, 8, 8));

        panelEntrada.add(new JLabel("N vehiculos:"));
        panelEntrada.add(new JLabel("Lambda:"));
        panelEntrada.add(new JLabel("Trimestre:"));
        panelEntrada.add(new JLabel("Semilla:"));
        panelEntrada.add(new JLabel("Precio inicial:"));
        panelEntrada.add(new JLabel("Incremento:"));
        panelEntrada.add(new JLabel("Precio internacional:"));
        panelEntrada.add(new JLabel("Accion:"));

        panelEntrada.add(txtN);
        panelEntrada.add(txtLambda);
        panelEntrada.add(txtTrimestre);
        panelEntrada.add(txtSemilla);
        panelEntrada.add(txtPrecioInicial);
        panelEntrada.add(txtIncremento);
        panelEntrada.add(txtPrecioInternacional);

        JButton btnEjecutar = new JButton("Ejecutar simulacion");
        btnEjecutar.addActionListener(e -> ejecutarSimulacion());
        panelEntrada.add(btnEjecutar);

        configurarTablaVehiculos();

        JTable tabla = new JTable(modeloTablaVehiculos);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        panel.add(panelEntrada, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout());

        modeloResumen.addColumn("Indicador");
        modeloResumen.addColumn("Valor");

        modeloUtilizacion.addColumn("Surtidor");
        modeloUtilizacion.addColumn("Vehiculos atendidos");
        modeloUtilizacion.addColumn("Utilizacion");

        JTable tablaResumen = new JTable(modeloResumen);
        JTable tablaUtilizacion = new JTable(modeloUtilizacion);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tablaResumen),
                new JScrollPane(tablaUtilizacion)
        );

        split.setDividerLocation(320);

        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    private void configurarTablaVehiculos() {
        modeloTablaVehiculos.addColumn("ID");
        modeloTablaVehiculos.addColumn("HL");
        modeloTablaVehiculos.addColumn("Tipo");
        modeloTablaVehiculos.addColumn("Carburante");
        modeloTablaVehiculos.addColumn("Litros");
        modeloTablaVehiculos.addColumn("TS");
        modeloTablaVehiculos.addColumn("Wsub");
        modeloTablaVehiculos.addColumn("Wint");
        modeloTablaVehiculos.addColumn("Csub");
        modeloTablaVehiculos.addColumn("Cint");
        modeloTablaVehiculos.addColumn("Decision");
        modeloTablaVehiculos.addColumn("Surtidor");
        modeloTablaVehiculos.addColumn("Inicio");
        modeloTablaVehiculos.addColumn("Fin");
        modeloTablaVehiculos.addColumn("WT");
    }

    private void ejecutarSimulacion() {
        try {
            int n = leerEntero(txtN.getText(), "N vehiculos");
            double lambda = leerDecimal(txtLambda.getText(), "Lambda");
            int trimestre = leerEntero(txtTrimestre.getText(), "Trimestre");
            long semilla = leerLong(txtSemilla.getText(), "Semilla");
            double precioInicial = leerDecimal(txtPrecioInicial.getText(), "Precio inicial");
            double incremento = leerDecimal(txtIncremento.getText(), "Incremento trimestral");
            double precioInternacional = leerDecimal(txtPrecioInternacional.getText(), "Precio internacional");

            if (n <= 0) {
                throw new IllegalArgumentException("N debe ser mayor a 0.");
            }

            if (lambda <= 0) {
                throw new IllegalArgumentException("Lambda debe ser mayor a 0.");
            }

            Simulador simulador = new Simulador(
                    n,
                    lambda,
                    precioInicial,
                    incremento,
                    precioInternacional,
                    trimestre,
                    semilla
            );

            List<ResultadoVehiculo> resultados = simulador.ejecutar();
            ResumenSimulacion resumen = ResumenSimulacion.desde(resultados, simulador);

            llenarTablaVehiculos(resultados);
            llenarResumen(resumen, simulador);
            llenarUtilizacion(resumen);
            panelGraficos.setResumen(resumen);

            JOptionPane.showMessageDialog(
                    this,
                    "Simulacion ejecutada correctamente con " + n + " vehiculos.",
                    "Resultado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error en los datos ingresados: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void llenarTablaVehiculos(List<ResultadoVehiculo> resultados) {
        modeloTablaVehiculos.setRowCount(0);

        for (ResultadoVehiculo r : resultados) {
            modeloTablaVehiculos.addRow(new Object[]{
                    r.getId(),
                    formato(r.getHoraLlegada()),
                    r.getTipoVehiculo(),
                    r.getCarburante(),
                    formato(r.getLitros()),
                    formato(r.getTiempoServicio()),
                    formato(r.getEsperaSub()),
                    formato(r.getEsperaInt()),
                    formato(r.getCostoSub()),
                    formato(r.getCostoInt()),
                    r.getDecision(),
                    r.getSurtidor(),
                    formato(r.getInicio()),
                    formato(r.getFin()),
                    formato(r.getTiempoTotal())
            });
        }
    }

    private void llenarResumen(ResumenSimulacion resumen, Simulador simulador) {
        modeloResumen.setRowCount(0);

        modeloResumen.addRow(new Object[]{"Vehiculos simulados", resumen.getTotalVehiculos()});
        modeloResumen.addRow(new Object[]{"Lambda de llegadas", formato(simulador.getLambda()) + " vehiculos/min"});
        modeloResumen.addRow(new Object[]{"Trimestre simulado", simulador.getTrimestre()});
        modeloResumen.addRow(new Object[]{"Precio subvencionado", "Bs. " + formato(simulador.getPrecioSubvencionadoActual()) + "/L"});
        modeloResumen.addRow(new Object[]{"Precio internacional", "Bs. " + formato(simulador.getPrecioInternacional()) + "/L"});
        modeloResumen.addRow(new Object[]{"Ruta subvencionada", resumen.getCantidadSubvencionada() + " vehiculos"});
        modeloResumen.addRow(new Object[]{"Porcentaje subvencionado", formato(resumen.getPorcentajeSubvencionado()) + "%"});
        modeloResumen.addRow(new Object[]{"Ruta internacional", resumen.getCantidadInternacional() + " vehiculos"});
        modeloResumen.addRow(new Object[]{"Porcentaje internacional", formato(resumen.getPorcentajeInternacional()) + "%"});
        modeloResumen.addRow(new Object[]{"Espera promedio", formato(resumen.getEsperaPromedio()) + " min"});
        modeloResumen.addRow(new Object[]{"Servicio promedio", formato(resumen.getServicioPromedio()) + " min"});
        modeloResumen.addRow(new Object[]{"Tiempo total promedio", formato(resumen.getTiempoTotalPromedio()) + " min"});
        modeloResumen.addRow(new Object[]{"Costo promedio", "Bs. " + formato(resumen.getCostoPromedio())});
        modeloResumen.addRow(new Object[]{"Horizonte simulado", formato(resumen.getHorizonteSimulado()) + " min"});
    }

    private void llenarUtilizacion(ResumenSimulacion resumen) {
        modeloUtilizacion.setRowCount(0);

        for (Map.Entry<String, Integer> entry : resumen.getAtendidosPorSurtidor().entrySet()) {
            String surtidor = entry.getKey();
            int atendidos = entry.getValue();
            double utilizacion = resumen.getUtilizacionPorSurtidor().get(surtidor);

            modeloUtilizacion.addRow(new Object[]{
                    surtidor,
                    atendidos,
                    formato(utilizacion) + "%"
            });
        }
    }

    private int leerEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser entero.");
        }
    }

    private long leerLong(String texto, String campo) {
        try {
            return Long.parseLong(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser entero.");
        }
    }

    private double leerDecimal(String texto, String campo) {
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser decimal.");
        }
    }

    private String formato(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazSimulador ventana = new InterfazSimulador();
            ventana.setVisible(true);
        });
    }
}