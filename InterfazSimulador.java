// InterfazSimulador.java
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;

public class InterfazSimulador extends JFrame {

    private final JTextField txtN;
    private final JTextField txtLambda;
    private final JTextField txtTrimestre;
    private final JTextField txtSemilla;
    private final JTextField txtPrecioInicial;
    private final JTextField txtIncremento;
    private final JTextField txtPrecioInternacional;
    private final JTextField txtInventarioInicial;
    private final JTextField txtCapacidadMaxima;
    private final JTextField txtNivelMinimo;
    private final JTextField txtTiempoCisternaMin;
    private final JTextField txtTiempoCisternaMax;
    private final JTextField txtCargaCisternaMin;
    private final JTextField txtCargaCisternaMax;
    private final JTextField txtDescargaMin;
    private final JTextField txtDescargaMax;

    private final Map<String, JTextField[]> camposPerfiles;

    private final DefaultTableModel modeloTablaVehiculos;
    private final DefaultTableModel modeloResumenOperativo;
    private final DefaultTableModel modeloResumenEconomico;
    private final DefaultTableModel modeloResumenAbastecimiento;
    private final DefaultTableModel modeloUtilizacion;

    private final GraficoResultadosPanel panelGraficos;

    public InterfazSimulador() {
        Locale.setDefault(Locale.US);

        setTitle("Simulador de Surtidores de Combustibles - Java");
        setSize(1400, 820);
        setMinimumSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        txtN = new JTextField();
        txtLambda = new JTextField();
        txtTrimestre = new JTextField();
        txtSemilla = new JTextField();
        txtPrecioInicial = new JTextField();
        txtIncremento = new JTextField();
        txtPrecioInternacional = new JTextField();
        txtInventarioInicial = new JTextField();
        txtCapacidadMaxima = new JTextField();
        txtNivelMinimo = new JTextField();
        txtTiempoCisternaMin = new JTextField();
        txtTiempoCisternaMax = new JTextField();
        txtCargaCisternaMin = new JTextField();
        txtCargaCisternaMax = new JTextField();
        txtDescargaMin = new JTextField();
        txtDescargaMax = new JTextField();
        camposPerfiles = new LinkedHashMap<>();

        modeloTablaVehiculos = new DefaultTableModel();
        modeloResumenOperativo = new DefaultTableModel();
        modeloResumenEconomico = new DefaultTableModel();
        modeloResumenAbastecimiento = new DefaultTableModel();
        modeloUtilizacion = new DefaultTableModel();
        panelGraficos = new GraficoResultadosPanel();

        configurarModelos();
        restaurarValoresPorDefecto();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Parametros", crearPanelParametros());
        tabs.addTab("Resultados por vehiculo", crearPanelTabla(modeloTablaVehiculos));
        tabs.addTab("Resumen operativo", crearPanelTabla(modeloResumenOperativo));
        tabs.addTab("Resumen economico", crearPanelTabla(modeloResumenEconomico));
        tabs.addTab("Resumen de abastecimiento", crearPanelTabla(modeloResumenAbastecimiento));
        tabs.addTab("Utilizacion de surtidores", crearPanelTabla(modeloUtilizacion));
        tabs.addTab("Graficas", new JScrollPane(panelGraficos));

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel crearPanelParametros() {
        JPanel contenedor = new JPanel(new BorderLayout(10, 10));
        contenedor.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel secciones = new JPanel(new GridLayout(1, 3, 12, 12));
        secciones.add(crearPanelParametrosGenerales());
        secciones.add(crearPanelPerfiles());
        secciones.add(crearPanelAbastecimiento());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        JButton btnEjecutar = new JButton("Ejecutar simulacion");
        JButton btnLimpiar = new JButton("Limpiar resultados");
        JButton btnRestaurar = new JButton("Restaurar valores por defecto");

        btnEjecutar.addActionListener(e -> ejecutarSimulacion());
        btnLimpiar.addActionListener(e -> limpiarResultados());
        btnRestaurar.addActionListener(e -> restaurarValoresPorDefecto());

        botones.add(btnLimpiar);
        botones.add(btnRestaurar);
        botones.add(btnEjecutar);

        contenedor.add(secciones, BorderLayout.CENTER);
        contenedor.add(botones, BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel crearPanelParametrosGenerales() {
        JPanel panel = crearPanelTitulado("Parametros generales", 7);
        agregarCampo(panel, "N vehiculos", txtN);
        agregarCampo(panel, "Lambda", txtLambda);
        agregarCampo(panel, "Trimestre", txtTrimestre);
        agregarCampo(panel, "Semilla", txtSemilla);
        agregarCampo(panel, "Precio inicial subv.", txtPrecioInicial);
        agregarCampo(panel, "Incremento trimestral", txtIncremento);
        agregarCampo(panel, "Precio internacional", txtPrecioInternacional);
        return panel;
    }

    private JPanel crearPanelPerfiles() {
        JPanel panel = crearPanelTitulado("Perfiles de vehiculos", 5);
        agregarPerfil(panel, "Particular", "20", "45", "3", "6", "30");
        agregarPerfil(panel, "Publico", "25", "60", "4", "7", "60");
        agregarPerfil(panel, "Interprovincial", "60", "150", "5", "9", "100");
        agregarPerfil(panel, "Pesado", "200", "400", "7", "12", "150");
        return panel;
    }

    private JPanel crearPanelAbastecimiento() {
        JPanel panel = crearPanelTitulado("Abastecimiento por cisternas", 9);
        agregarCampo(panel, "Inventario inicial", txtInventarioInicial);
        agregarCampo(panel, "Capacidad maxima", txtCapacidadMaxima);
        agregarCampo(panel, "Nivel minimo", txtNivelMinimo);
        agregarCampo(panel, "Tiempo cisterna min", txtTiempoCisternaMin);
        agregarCampo(panel, "Tiempo cisterna max", txtTiempoCisternaMax);
        agregarCampo(panel, "Carga cisterna min", txtCargaCisternaMin);
        agregarCampo(panel, "Carga cisterna max", txtCargaCisternaMax);
        agregarCampo(panel, "Descarga min", txtDescargaMin);
        agregarCampo(panel, "Descarga max", txtDescargaMax);
        return panel;
    }

    private JPanel crearPanelTitulado(String titulo, int filas) {
        JPanel panel = new JPanel(new GridLayout(filas, 2, 8, 8));
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                borde,
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return panel;
    }

    private void agregarCampo(JPanel panel, String etiqueta, JTextField campo) {
        panel.add(new JLabel(etiqueta + ":"));
        panel.add(campo);
    }

    private void agregarPerfil(
            JPanel panel,
            String tipo,
            String litrosMin,
            String litrosMax,
            String servicioMin,
            String servicioMax,
            String costoOportunidad
    ) {
        JPanel fila = new JPanel(new GridLayout(1, 5, 4, 4));
        JTextField txtLitrosMin = new JTextField(litrosMin);
        JTextField txtLitrosMax = new JTextField(litrosMax);
        JTextField txtServicioMin = new JTextField(servicioMin);
        JTextField txtServicioMax = new JTextField(servicioMax);
        JTextField txtCosto = new JTextField(costoOportunidad);

        fila.add(txtLitrosMin);
        fila.add(txtLitrosMax);
        fila.add(txtServicioMin);
        fila.add(txtServicioMax);
        fila.add(txtCosto);

        camposPerfiles.put(tipo, new JTextField[]{
                txtLitrosMin,
                txtLitrosMax,
                txtServicioMin,
                txtServicioMax,
                txtCosto
        });

        panel.add(new JLabel(tipo + " Lmin/Lmax TSmin/TSmax Costo:"));
        panel.add(fila);
    }

    private JPanel crearPanelTabla(DefaultTableModel modelo) {
        JPanel panel = new JPanel(new BorderLayout());
        JTable tabla = new JTable(modelo);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setRowHeight(24);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void configurarModelos() {
        agregarColumnas(modeloTablaVehiculos,
                "i", "HL", "tipoVehiculo", "carburante", "litros", "TS",
                "Wsub", "Wint", "Csub", "Cint", "ruta", "surtidor",
                "inicio", "fin", "esperaReal", "WT", "estado",
                "ingresoGenerado", "perdidaEstimada", "inventarioAntes",
                "inventarioDespues"
        );

        agregarColumnas(modeloResumenOperativo, "Indicador", "Valor");
        agregarColumnas(modeloResumenEconomico, "Indicador", "Valor");
        agregarColumnas(modeloResumenAbastecimiento, "Indicador", "Valor");
        agregarColumnas(modeloUtilizacion, "Surtidor", "Vehiculos atendidos", "Utilizacion");
    }

    private void agregarColumnas(DefaultTableModel modelo, String... columnas) {
        for (String columna : columnas) {
            modelo.addColumn(columna);
        }
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
            double inventarioInicial = leerDecimal(txtInventarioInicial.getText(), "Inventario inicial");
            double capacidadMaxima = leerDecimal(txtCapacidadMaxima.getText(), "Capacidad maxima");
            double nivelMinimo = leerDecimal(txtNivelMinimo.getText(), "Nivel minimo");
            double tiempoCisternaMin = leerDecimal(txtTiempoCisternaMin.getText(), "Tiempo minimo entre cisternas");
            double tiempoCisternaMax = leerDecimal(txtTiempoCisternaMax.getText(), "Tiempo maximo entre cisternas");
            double cargaCisternaMin = leerDecimal(txtCargaCisternaMin.getText(), "Carga minima de cisterna");
            double cargaCisternaMax = leerDecimal(txtCargaCisternaMax.getText(), "Carga maxima de cisterna");
            double descargaMin = leerDecimal(txtDescargaMin.getText(), "Descarga minima");
            double descargaMax = leerDecimal(txtDescargaMax.getText(), "Descarga maxima");

            validarParametros(n, lambda, capacidadMaxima, tiempoCisternaMin, tiempoCisternaMax,
                    cargaCisternaMin, cargaCisternaMax, descargaMin, descargaMax);

            Simulador simulador = new Simulador(
                    n,
                    lambda,
                    precioInicial,
                    incremento,
                    precioInternacional,
                    trimestre,
                    semilla,
                    inventarioInicial,
                    capacidadMaxima,
                    nivelMinimo,
                    tiempoCisternaMin,
                    tiempoCisternaMax,
                    cargaCisternaMin,
                    cargaCisternaMax,
                    descargaMin,
                    descargaMax
            );

            configurarPerfiles(simulador);

            List<ResultadoVehiculo> resultados = simulador.ejecutar();
            ResumenSimulacion resumen = ResumenSimulacion.desde(resultados, simulador);

            llenarTablaVehiculos(resultados);
            llenarResumenOperativo(resumen);
            llenarResumenEconomico(resumen);
            llenarResumenAbastecimiento(resumen, simulador);
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

    private void configurarPerfiles(Simulador simulador) {
        for (Map.Entry<String, JTextField[]> entry : camposPerfiles.entrySet()) {
            JTextField[] campos = entry.getValue();
            double litrosMin = leerDecimal(campos[0].getText(), entry.getKey() + " litros minimos");
            double litrosMax = leerDecimal(campos[1].getText(), entry.getKey() + " litros maximos");
            double servicioMin = leerDecimal(campos[2].getText(), entry.getKey() + " servicio minimo");
            double servicioMax = leerDecimal(campos[3].getText(), entry.getKey() + " servicio maximo");
            double costo = leerDecimal(campos[4].getText(), entry.getKey() + " costo de oportunidad");

            if (litrosMin < 0 || litrosMax < litrosMin) {
                throw new IllegalArgumentException("Rango de litros invalido en " + entry.getKey() + ".");
            }

            if (servicioMin < 0 || servicioMax < servicioMin) {
                throw new IllegalArgumentException("Rango de servicio invalido en " + entry.getKey() + ".");
            }

            if (costo < 0) {
                throw new IllegalArgumentException("Costo de oportunidad invalido en " + entry.getKey() + ".");
            }

            simulador.configurarPerfil(entry.getKey(), litrosMin, litrosMax, servicioMin, servicioMax, costo);
        }
    }

    private void validarParametros(
            int n,
            double lambda,
            double capacidadMaxima,
            double tiempoCisternaMin,
            double tiempoCisternaMax,
            double cargaCisternaMin,
            double cargaCisternaMax,
            double descargaMin,
            double descargaMax
    ) {
        if (n <= 0) {
            throw new IllegalArgumentException("N debe ser mayor a 0.");
        }

        if (lambda <= 0) {
            throw new IllegalArgumentException("Lambda debe ser mayor a 0.");
        }

        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad maxima debe ser mayor a 0.");
        }

        if (tiempoCisternaMin < 0 || tiempoCisternaMax < tiempoCisternaMin) {
            throw new IllegalArgumentException("El rango de tiempo entre cisternas no es valido.");
        }

        if (cargaCisternaMin < 0 || cargaCisternaMax < cargaCisternaMin) {
            throw new IllegalArgumentException("El rango de carga de cisterna no es valido.");
        }

        if (descargaMin < 0 || descargaMax < descargaMin) {
            throw new IllegalArgumentException("El rango de descarga no es valido.");
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
                    formato(r.getEsperaReal()),
                    formato(r.getTiempoTotal()),
                    r.getEstado(),
                    formato(r.getIngresoGenerado()),
                    formato(r.getPerdidaEstimada()),
                    formato(r.getInventarioAntes()),
                    formato(r.getInventarioDespues())
            });
        }
    }

    private void llenarResumenOperativo(ResumenSimulacion resumen) {
        modeloResumenOperativo.setRowCount(0);
        modeloResumenOperativo.addRow(new Object[]{"Vehiculos simulados", resumen.getVehiculosSimulados()});
        modeloResumenOperativo.addRow(new Object[]{"Atendidos", resumen.getVehiculosAtendidos()});
        modeloResumenOperativo.addRow(new Object[]{"No atendidos", resumen.getVehiculosNoAtendidos()});
        modeloResumenOperativo.addRow(new Object[]{"Ruta subvencionada", resumen.getCantidadSubvencionada() + " vehiculos"});
        modeloResumenOperativo.addRow(new Object[]{"Ruta internacional", resumen.getCantidadInternacional() + " vehiculos"});
        modeloResumenOperativo.addRow(new Object[]{"Espera promedio", formato(resumen.getEsperaPromedio()) + " min"});
        modeloResumenOperativo.addRow(new Object[]{"Servicio promedio", formato(resumen.getServicioPromedio()) + " min"});
        modeloResumenOperativo.addRow(new Object[]{"Tiempo total promedio", formato(resumen.getTiempoTotalPromedio()) + " min"});
        modeloResumenOperativo.addRow(new Object[]{"Costo promedio", "Bs. " + formato(resumen.getCostoPromedio())});
    }

    private void llenarResumenEconomico(ResumenSimulacion resumen) {
        modeloResumenEconomico.setRowCount(0);
        modeloResumenEconomico.addRow(new Object[]{"Litros vendidos subvencionados", formato(resumen.getLitrosVendidosSubvencionados()) + " L"});
        modeloResumenEconomico.addRow(new Object[]{"Litros vendidos internacionales", formato(resumen.getLitrosVendidosInternacionales()) + " L"});
        modeloResumenEconomico.addRow(new Object[]{"Litros vendidos totales", formato(resumen.getLitrosVendidosTotales()) + " L"});
        modeloResumenEconomico.addRow(new Object[]{"Ingreso subvencionado", "Bs. " + formato(resumen.getIngresoSubvencionado())});
        modeloResumenEconomico.addRow(new Object[]{"Ingreso internacional", "Bs. " + formato(resumen.getIngresoInternacional())});
        modeloResumenEconomico.addRow(new Object[]{"Ingreso total", "Bs. " + formato(resumen.getIngresoTotal())});
        modeloResumenEconomico.addRow(new Object[]{"Litros no vendidos", formato(resumen.getLitrosNoVendidos()) + " L"});
        modeloResumenEconomico.addRow(new Object[]{"Perdida estimada", "Bs. " + formato(resumen.getPerdidaEstimada())});
    }

    private void llenarResumenAbastecimiento(ResumenSimulacion resumen, Simulador simulador) {
        modeloResumenAbastecimiento.setRowCount(0);
        modeloResumenAbastecimiento.addRow(new Object[]{"Inventario inicial", formato(resumen.getInventarioInicial()) + " L"});
        modeloResumenAbastecimiento.addRow(new Object[]{"Inventario final", formato(resumen.getInventarioFinal()) + " L"});
        modeloResumenAbastecimiento.addRow(new Object[]{"Capacidad maxima", formato(simulador.getCapacidadMaxima()) + " L"});
        modeloResumenAbastecimiento.addRow(new Object[]{"Nivel minimo", formato(simulador.getNivelMinimo()) + " L"});
        modeloResumenAbastecimiento.addRow(new Object[]{"Cisternas recibidas", resumen.getCisternasRecibidas()});
        modeloResumenAbastecimiento.addRow(new Object[]{"Litros abastecidos reales", formato(resumen.getLitrosAbastecidosReales()) + " L"});
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

    private void limpiarResultados() {
        modeloTablaVehiculos.setRowCount(0);
        modeloResumenOperativo.setRowCount(0);
        modeloResumenEconomico.setRowCount(0);
        modeloResumenAbastecimiento.setRowCount(0);
        modeloUtilizacion.setRowCount(0);
        panelGraficos.setResumen(null);
    }

    private void restaurarValoresPorDefecto() {
        txtN.setText("500");
        txtLambda.setText("1.50");
        txtTrimestre.setText("4");
        txtSemilla.setText("100");
        txtPrecioInicial.setText("6.96");
        txtIncremento.setText("1.32");
        txtPrecioInternacional.setText("12.36");
        txtInventarioInicial.setText("3000");
        txtCapacidadMaxima.setText("30000");
        txtNivelMinimo.setText("3000");
        txtTiempoCisternaMin.setText("10");
        txtTiempoCisternaMax.setText("20");
        txtCargaCisternaMin.setText("15000");
        txtCargaCisternaMax.setText("25000");
        txtDescargaMin.setText("5");
        txtDescargaMax.setText("10");

        setPerfil("Particular", "20", "45", "3", "6", "30");
        setPerfil("Publico", "25", "60", "4", "7", "60");
        setPerfil("Interprovincial", "60", "150", "5", "9", "100");
        setPerfil("Pesado", "200", "400", "7", "12", "150");
    }

    private void setPerfil(
            String tipo,
            String litrosMin,
            String litrosMax,
            String servicioMin,
            String servicioMax,
            String costo
    ) {
        JTextField[] campos = camposPerfiles.get(tipo);
        if (campos == null) {
            return;
        }

        campos[0].setText(litrosMin);
        campos[1].setText(litrosMax);
        campos[2].setText(servicioMin);
        campos[3].setText(servicioMax);
        campos[4].setText(costo);
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
