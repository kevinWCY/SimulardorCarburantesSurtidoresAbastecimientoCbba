// InterfazSimulador.java
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InterfazSimulador extends JFrame {

    private static final Font FUENTE_BASE = new Font("SansSerif", Font.PLAIN, 13);
    private static final Dimension TAMANIO_CAMPO = new Dimension(110, 26);

    private final JTextField txtN;
    private final JTextField txtLambda;
    private final JTextField txtTrimestre;
    private final JComboBox<String> cboPeriodicidad;
    private final JTextField txtSemilla;
    private final JTextField txtInventarioInicial;
    private final JTextField txtCapacidadMaxima;
    private final JTextField txtNivelMinimo;
    private final JTextField txtTiempoCisternaMin;
    private final JTextField txtTiempoCisternaMax;
    private final JTextField txtCargaCisternaMin;
    private final JTextField txtCargaCisternaMax;
    private final JTextField txtDescargaMin;
    private final JTextField txtDescargaMax;

    private final DefaultTableModel modeloPerfiles;
    private final DefaultTableModel modeloPrecios;
    private final DefaultTableModel modeloTablaVehiculos;
    private final DefaultTableModel modeloResumenOperativo;
    private final DefaultTableModel modeloResumenEconomico;
    private final DefaultTableModel modeloResumenAbastecimiento;
    private final DefaultTableModel modeloUtilizacion;

    private final GraficoResultadosPanel panelGraficos;

    public InterfazSimulador() {
        Locale.setDefault(Locale.US);

        setTitle("Simulador de Surtidores de Combustibles - Java");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1060, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        txtN = crearCampo();
        txtLambda = crearCampo();
        txtTrimestre = crearCampo();
        cboPeriodicidad = new JComboBox<>(new String[]{"Mensual", "Bimestral", "Trimestral", "Semestral", "Anual"});
        cboPeriodicidad.setFont(FUENTE_BASE);
        txtSemilla = crearCampo();
        txtInventarioInicial = crearCampo();
        txtCapacidadMaxima = crearCampo();
        txtNivelMinimo = crearCampo();
        txtTiempoCisternaMin = crearCampo();
        txtTiempoCisternaMax = crearCampo();
        txtCargaCisternaMin = crearCampo();
        txtCargaCisternaMax = crearCampo();
        txtDescargaMin = crearCampo();
        txtDescargaMax = crearCampo();

        modeloPerfiles = crearModeloPerfiles();
        modeloPrecios = crearModeloPrecios();
        modeloTablaVehiculos = new DefaultTableModel();
        modeloResumenOperativo = new DefaultTableModel();
        modeloResumenEconomico = new DefaultTableModel();
        modeloResumenAbastecimiento = new DefaultTableModel();
        modeloUtilizacion = new DefaultTableModel();
        panelGraficos = new GraficoResultadosPanel();

        configurarModelos();
        restaurarValoresPorDefecto();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FUENTE_BASE);
        tabs.addTab("Parametros", crearPanelParametros());
        tabs.addTab("Resultados por vehiculo", crearPanelResultadosVehiculo());
        tabs.addTab("Resumen general", crearPanelResumenGeneral());
        tabs.addTab("Graficas", new JScrollPane(panelGraficos));

        add(tabs, BorderLayout.CENTER);
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setFont(FUENTE_BASE);
        campo.setPreferredSize(TAMANIO_CAMPO);
        campo.setMinimumSize(TAMANIO_CAMPO);
        return campo;
    }

    private JPanel crearPanelParametros() {
        JPanel contenedor = new JPanel(new BorderLayout(8, 8));
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filaSuperior = new JPanel(new GridLayout(1, 2, 8, 8));
        filaSuperior.add(crearPanelParametrosGenerales());
        filaSuperior.add(crearPanelPrecios());

        JPanel filaCentral = new JPanel(new GridLayout(1, 2, 8, 8));
        filaCentral.add(crearPanelPerfiles());
        filaCentral.add(crearPanelAbastecimiento());

        JPanel centro = new JPanel(new GridLayout(2, 1, 8, 8));
        centro.add(filaSuperior);
        centro.add(filaCentral);

        contenedor.add(centro, BorderLayout.CENTER);
        contenedor.add(crearBarraAcciones(), BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel crearPanelParametrosGenerales() {
        JPanel panel = crearPanelFormulario("Parametros generales");
        agregarCampo(panel, "N vehiculos", txtN, 0);
        agregarCampo(panel, "Lambda", txtLambda, 1);
        agregarCampo(panel, "Cantidad de periodos", txtTrimestre, 2);
        agregarCombo(panel, "Periodicidad", cboPeriodicidad, 3);
        agregarCampo(panel, "Semilla", txtSemilla, 4);
        return panel;
    }

    private JPanel crearPanelPrecios() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(crearBorde("Precios por carburante"));

        JTable tabla = crearTabla(modeloPrecios, 24);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        ajustarAnchos(tabla, 110, 160, 150, 140);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPerfiles() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(crearBorde("Perfiles de vehiculos"));

        JTable tabla = crearTabla(modeloPerfiles, 22);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        ajustarAnchos(tabla, 120, 55, 55, 55, 55, 70);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelAbastecimiento() {
        JPanel panel = crearPanelFormulario("Abastecimiento por cisternas");
        agregarCampo(panel, "Inventario inicial", txtInventarioInicial, 0);
        agregarCampo(panel, "Capacidad maxima", txtCapacidadMaxima, 1);
        agregarCampo(panel, "Nivel minimo", txtNivelMinimo, 2);
        agregarCampo(panel, "Tiempo cisterna min", txtTiempoCisternaMin, 3);
        agregarCampo(panel, "Tiempo cisterna max", txtTiempoCisternaMax, 4);
        agregarCampo(panel, "Carga cisterna min", txtCargaCisternaMin, 5);
        agregarCampo(panel, "Carga cisterna max", txtCargaCisternaMax, 6);
        agregarCampo(panel, "Descarga min", txtDescargaMin, 7);
        agregarCampo(panel, "Descarga max", txtDescargaMax, 8);
        return panel;
    }

    private JPanel crearPanelFormulario(String titulo) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(crearBorde(titulo));
        return panel;
    }

    private TitledBorder crearBorde(String titulo) {
        TitledBorder borde = BorderFactory.createTitledBorder(titulo);
        borde.setTitleFont(FUENTE_BASE.deriveFont(Font.BOLD));
        return borde;
    }

    private void agregarCampo(JPanel panel, String etiqueta, JTextField campo, int fila) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = fila;
        label.anchor = GridBagConstraints.WEST;
        label.insets = new Insets(3, 8, 3, 8);
        panel.add(new JLabel(etiqueta + ":"), label);

        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = fila;
        input.anchor = GridBagConstraints.WEST;
        input.insets = new Insets(3, 2, 3, 8);
        panel.add(campo, input);
    }

    private void agregarCombo(JPanel panel, String etiqueta, JComboBox<String> combo, int fila) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = fila;
        label.anchor = GridBagConstraints.WEST;
        label.insets = new Insets(3, 8, 3, 8);
        panel.add(new JLabel(etiqueta + ":"), label);

        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = fila;
        input.anchor = GridBagConstraints.WEST;
        input.insets = new Insets(3, 2, 3, 8);
        combo.setPreferredSize(new Dimension(130, 26));
        panel.add(combo, input);
    }

    private JPanel crearBarraAcciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBorde("Acciones"));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        JButton btnLimpiar = new JButton("Limpiar resultados");
        JButton btnRestaurar = new JButton("Restaurar valores por defecto");
        JButton btnEjecutar = new JButton("Ejecutar simulacion");

        btnLimpiar.addActionListener(e -> limpiarResultados());
        btnRestaurar.addActionListener(e -> restaurarValoresPorDefecto());
        btnEjecutar.addActionListener(e -> ejecutarSimulacion());

        botones.add(btnLimpiar);
        botones.add(btnRestaurar);
        botones.add(btnEjecutar);
        panel.add(botones, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelResultadosVehiculo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTable tabla = crearTabla(modeloTablaVehiculos, 23);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        ajustarAnchos(tabla, 45, 65, 130, 95, 75, 60, 70, 70, 85, 85, 115,
                70, 70, 70, 85, 70, 95, 105, 115, 115, 115, 115, 130);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelResumenGeneral() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(crearPanelTablaResumen("Resumen operativo", modeloResumenOperativo));
        panel.add(crearPanelTablaResumen("Resumen economico", modeloResumenEconomico));
        panel.add(crearPanelTablaResumen("Resumen de abastecimiento", modeloResumenAbastecimiento));
        panel.add(crearPanelTablaResumen("Utilizacion de surtidores", modeloUtilizacion));
        return panel;
    }

    private JPanel crearPanelTablaResumen(String titulo, DefaultTableModel modelo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(crearBorde(titulo));

        JTable tabla = crearTabla(modelo, 22);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JTable crearTabla(DefaultTableModel modelo, int altoFila) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(FUENTE_BASE);
        tabla.getTableHeader().setFont(FUENTE_BASE.deriveFont(Font.BOLD));
        tabla.setRowHeight(altoFila);
        tabla.setFillsViewportHeight(true);
        return tabla;
    }

    private void ajustarAnchos(JTable tabla, int... anchos) {
        TableColumnModel columnas = tabla.getColumnModel();
        for (int i = 0; i < anchos.length && i < columnas.getColumnCount(); i++) {
            columnas.getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void configurarModelos() {
        agregarColumnas(modeloTablaVehiculos,
                "i", "HL", "tipoVehiculo", "carburante", "litros", "TS",
                "Wsub", "Wint", "Csub", "Cint", "ruta", "surtidor",
                "inicio", "fin", "esperaReal", "WT", "estado",
                "precioAplicado", "ingresoGenerado", "perdidaEstimada",
                "inventarioAntes", "inventarioDespues", "motivoNoAtendido"
        );

        agregarColumnas(modeloResumenOperativo, "Indicador", "Valor");
        agregarColumnas(modeloResumenEconomico, "Indicador", "Valor");
        agregarColumnas(modeloResumenAbastecimiento, "Indicador", "Valor");
        agregarColumnas(modeloUtilizacion, "Surtidor", "Vehiculos atendidos", "Utilizacion");
    }

    private DefaultTableModel crearModeloPerfiles() {
        return new DefaultTableModel(new Object[]{"Perfil", "L min", "L max", "TS min", "TS max", "CO Bs/h"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
    }

    private DefaultTableModel crearModeloPrecios() {
        return new DefaultTableModel(new Object[]{
                "Carburante",
                "Precio base subvencionado",
                "Incremento por periodo",
                "Precio internacional"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
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
            int trimestre = leerEntero(txtTrimestre.getText(), "Cantidad de periodos");
            String periodicidad = String.valueOf(cboPeriodicidad.getSelectedItem());
            long semilla = leerLong(txtSemilla.getText(), "Semilla");
            double gasolinaBaseSub = leerPrecio("Gasolina", 1);
            double gasolinaIncremento = leerPrecio("Gasolina", 2);
            double gasolinaInt = leerPrecio("Gasolina", 3);
            double dieselBaseSub = leerPrecio("Diesel", 1);
            double dieselIncremento = leerPrecio("Diesel", 2);
            double dieselInt = leerPrecio("Diesel", 3);
            double gnvBaseSub = leerPrecio("GNV", 1);
            double gnvIncremento = leerPrecio("GNV", 2);
            double gnvInt = leerPrecio("GNV", 3);
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
            validarPrecios(trimestre);

            Simulador simulador = new Simulador(
                    n,
                    lambda,
                    gasolinaBaseSub,
                    gasolinaIncremento,
                    gasolinaInt,
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
                    descargaMax,
                    gasolinaBaseSub,
                    gasolinaIncremento,
                    gasolinaInt,
                    dieselBaseSub,
                    dieselIncremento,
                    dieselInt,
                    gnvBaseSub,
                    gnvIncremento,
                    gnvInt
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
                    "Simulacion ejecutada correctamente con " + n
                            + " vehiculos. Periodicidad: " + periodicidad + ".",
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
        for (int fila = 0; fila < modeloPerfiles.getRowCount(); fila++) {
            String perfilVisible = String.valueOf(modeloPerfiles.getValueAt(fila, 0));
            String perfilSimulador = nombrePerfilSimulador(perfilVisible);
            double litrosMin = leerDecimal(valorTabla(fila, 1), perfilVisible + " litros minimos");
            double litrosMax = leerDecimal(valorTabla(fila, 2), perfilVisible + " litros maximos");
            double servicioMin = leerDecimal(valorTabla(fila, 3), perfilVisible + " servicio minimo");
            double servicioMax = leerDecimal(valorTabla(fila, 4), perfilVisible + " servicio maximo");
            double costo = leerDecimal(valorTabla(fila, 5), perfilVisible + " costo de oportunidad");

            if (litrosMin < 0 || litrosMax < litrosMin) {
                throw new IllegalArgumentException("Rango de litros invalido en " + perfilVisible + ".");
            }

            if (servicioMin < 0 || servicioMax < servicioMin) {
                throw new IllegalArgumentException("Rango de servicio invalido en " + perfilVisible + ".");
            }

            if (costo < 0) {
                throw new IllegalArgumentException("Costo de oportunidad invalido en " + perfilVisible + ".");
            }

            simulador.configurarPerfil(perfilSimulador, litrosMin, litrosMax, servicioMin, servicioMax, costo);
        }
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloPerfiles.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private String nombrePerfilSimulador(String perfilVisible) {
        if (perfilVisible.equals("Publico urbano")) {
            return "Publico";
        }
        return perfilVisible;
    }

    private double leerPrecio(String carburante, int columna) {
        for (int fila = 0; fila < modeloPrecios.getRowCount(); fila++) {
            Object nombre = modeloPrecios.getValueAt(fila, 0);
            if (nombre != null && carburante.equalsIgnoreCase(nombre.toString())) {
                Object valor = modeloPrecios.getValueAt(fila, columna);
                return leerDecimal(valor == null ? "" : valor.toString(),
                        carburante + " " + modeloPrecios.getColumnName(columna));
            }
        }
        throw new IllegalArgumentException("No existe precio configurado para " + carburante + ".");
    }

    private void validarPrecios(int cantidadPeriodos) {
        if (cantidadPeriodos < 0) {
            throw new IllegalArgumentException("La cantidad de periodos no puede ser negativa.");
        }

        for (int fila = 0; fila < modeloPrecios.getRowCount(); fila++) {
            String carburante = String.valueOf(modeloPrecios.getValueAt(fila, 0));
            double precioBase = leerPrecio(carburante, 1);
            double incremento = leerPrecio(carburante, 2);
            double precioInternacional = leerPrecio(carburante, 3);

            if (precioBase < 0) {
                throw new IllegalArgumentException("El precio base subvencionado de " + carburante + " no puede ser negativo.");
            }

            if (incremento < 0) {
                throw new IllegalArgumentException("El incremento por periodo de " + carburante + " no puede ser negativo.");
            }

            if (precioInternacional < 0) {
                throw new IllegalArgumentException("El precio internacional de " + carburante + " no puede ser negativo.");
            }

            if (precioBase > precioInternacional) {
                throw new IllegalArgumentException("Advertencia: el precio base subvencionado de "
                        + carburante + " no debe superar su precio internacional.");
            }
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
                    formato(r.getPrecioAplicado()),
                    formato(r.getIngresoGenerado()),
                    formato(r.getPerdidaEstimada()),
                    formato(r.getInventarioAntes()),
                    formato(r.getInventarioDespues()),
                    r.getMotivoNoAtendido()
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
        cboPeriodicidad.setSelectedItem("Trimestral");
        txtSemilla.setText("100");
        txtInventarioInicial.setText("3000");
        txtCapacidadMaxima.setText("30000");
        txtNivelMinimo.setText("3000");
        txtTiempoCisternaMin.setText("10");
        txtTiempoCisternaMax.setText("20");
        txtCargaCisternaMin.setText("15000");
        txtCargaCisternaMax.setText("25000");
        txtDescargaMin.setText("5");
        txtDescargaMax.setText("10");

        modeloPerfiles.setRowCount(0);
        modeloPerfiles.addRow(new Object[]{"Particular", "20", "45", "3", "6", "30"});
        modeloPerfiles.addRow(new Object[]{"Publico urbano", "25", "60", "4", "7", "60"});
        modeloPerfiles.addRow(new Object[]{"Interprovincial", "60", "150", "5", "9", "100"});
        modeloPerfiles.addRow(new Object[]{"Pesado", "200", "400", "7", "12", "150"});

        modeloPrecios.setRowCount(0);
        modeloPrecios.addRow(new Object[]{"Gasolina", "6.96", "1.32", "8.68"});
        modeloPrecios.addRow(new Object[]{"Diesel", "9.80", "0.00", "9.80"});
        modeloPrecios.addRow(new Object[]{"GNV", "2.73", "0.00", "2.90"});
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

    private static void aplicarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

            UIManager.put("defaultFont", FUENTE_BASE);
            UIManager.put("Label.font", FUENTE_BASE);
            UIManager.put("Button.font", FUENTE_BASE);
            UIManager.put("TextField.font", FUENTE_BASE);
            UIManager.put("Table.font", FUENTE_BASE);
            UIManager.put("TableHeader.font", FUENTE_BASE.deriveFont(Font.BOLD));
            UIManager.put("TabbedPane.font", FUENTE_BASE);
            UIManager.put("TitledBorder.font", FUENTE_BASE.deriveFont(Font.BOLD));
        } catch (Exception ex) {
            System.err.println("No se pudo aplicar Nimbus. Se usara el Look and Feel por defecto.");
        }
    }

    public static void main(String[] args) {
        aplicarLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            InterfazSimulador ventana = new InterfazSimulador();
            ventana.setVisible(true);
        });
    }
}
