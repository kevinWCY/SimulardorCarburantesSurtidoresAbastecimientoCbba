// InterfazSimulador.java
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class InterfazSimulador extends JFrame {

    private static final Font FUENTE_BASE = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.PLAIN, 13);
    private static final Dimension TAMANIO_CAMPO = new Dimension(110, 26);
    private static final Color AZUL_PETROLEO = new Color(22, 84, 112);
    private static final Color AZUL_ACCION = new Color(35, 105, 145);
    private static final Color VERDE_ABASTECIMIENTO = new Color(56, 142, 93);
    private static final Color NARANJA_RUTA = new Color(221, 132, 48);
    private static final Color ROJO_ALERTA = new Color(184, 82, 82);
    private static final Color GRIS_FONDO = new Color(241, 244, 248);
    private static final Color GRIS_BORDE = new Color(207, 216, 226);
    private static final Color GRIS_SUAVE = new Color(232, 237, 243);
    private static final Color BLANCO_TARJETA = Color.WHITE;

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
    private List<ResultadoVehiculo> ultimosResultados;
    private ResumenSimulacion ultimoResumen;
    private Simulador ultimoSimulador;

    public InterfazSimulador() {
        Locale.setDefault(Locale.US);

        setTitle("Simulador de Surtidores de Combustibles - Java");
        setSize(1200, 750);
        setMinimumSize(new Dimension(1060, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(GRIS_FONDO);

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
        ultimosResultados = null;
        ultimoResumen = null;
        ultimoSimulador = null;

        configurarModelos();
        restaurarValoresPorDefecto();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FUENTE_BASE);
        tabs.setBackground(GRIS_FONDO);
        tabs.setForeground(AZUL_PETROLEO);
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
        contenedor.setBackground(GRIS_FONDO);
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filaSuperior = new JPanel(new GridLayout(1, 2, 8, 8));
        filaSuperior.setOpaque(false);
        filaSuperior.add(crearPanelParametrosGenerales());
        filaSuperior.add(crearPanelPrecios());

        JPanel filaCentral = new JPanel(new GridLayout(1, 2, 8, 8));
        filaCentral.setOpaque(false);
        filaCentral.add(crearPanelPerfiles());
        filaCentral.add(crearPanelAbastecimiento());

        JPanel centro = new JPanel(new GridLayout(2, 1, 8, 8));
        centro.setOpaque(false);
        centro.add(filaSuperior);
        centro.add(filaCentral);

        contenedor.add(crearEncabezado(), BorderLayout.NORTH);
        contenedor.add(centro, BorderLayout.CENTER);
        contenedor.add(crearBarraAcciones(), BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(10, 2));
        panel.setBackground(AZUL_PETROLEO);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titulo = new JLabel("Simulador de Abastecimiento de Carburantes");
        titulo.setFont(FUENTE_TITULO);
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("An\u00e1lisis de demanda, inventario, cisternas, ingresos y atenci\u00f3n en surtidores");
        subtitulo.setFont(FUENTE_SUBTITULO);
        subtitulo.setForeground(new Color(218, 235, 242));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        panel.add(textos, BorderLayout.CENTER);
        return panel;
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
        panel.setBackground(BLANCO_TARJETA);
        panel.setBorder(crearBorde("Precios por carburante"));

        JTable tabla = crearTabla(modeloPrecios, 24);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        ajustarAnchos(tabla, 110, 160, 150, 140);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPerfiles() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBackground(BLANCO_TARJETA);
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
        panel.setBackground(BLANCO_TARJETA);
        panel.setBorder(crearBorde(titulo));
        return panel;
    }

    private TitledBorder crearBorde(String titulo) {
        TitledBorder borde = BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GRIS_BORDE),
                        BorderFactory.createEmptyBorder(6, 8, 8, 8)
                ),
                titulo
        );
        borde.setTitleFont(FUENTE_BASE.deriveFont(Font.BOLD));
        borde.setTitleColor(AZUL_PETROLEO);
        return borde;
    }

    private void agregarCampo(JPanel panel, String etiqueta, JTextField campo, int fila) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = fila;
        label.anchor = GridBagConstraints.WEST;
        label.insets = new Insets(fila == 0 ? 10 : 4, 10, 4, 10);
        panel.add(new JLabel(etiqueta + ":"), label);

        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = fila;
        input.anchor = GridBagConstraints.WEST;
        input.insets = new Insets(fila == 0 ? 10 : 4, 2, 4, 10);
        panel.add(campo, input);
    }

    private void agregarCombo(JPanel panel, String etiqueta, JComboBox<String> combo, int fila) {
        GridBagConstraints label = new GridBagConstraints();
        label.gridx = 0;
        label.gridy = fila;
        label.anchor = GridBagConstraints.WEST;
        label.insets = new Insets(fila == 0 ? 10 : 4, 10, 4, 10);
        panel.add(new JLabel(etiqueta + ":"), label);

        GridBagConstraints input = new GridBagConstraints();
        input.gridx = 1;
        input.gridy = fila;
        input.anchor = GridBagConstraints.WEST;
        input.insets = new Insets(fila == 0 ? 10 : 4, 2, 4, 10);
        combo.setPreferredSize(new Dimension(130, 26));
        panel.add(combo, input);
    }

    private JPanel crearBarraAcciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BLANCO_TARJETA);
        panel.setBorder(crearBorde("Acciones"));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        botones.setOpaque(false);
        JButton btnEjecutar = new JButton("Ejecutar simulacion");
        JButton btnEscenarioBase = new JButton("Escenario base");
        JButton btnLimpiar = new JButton("Limpiar resultados");
        JButton btnRestaurar = new JButton("Restaurar valores por defecto");
        JButton btnGuardar = new JButton("Guardar configuracion");
        JButton btnCargar = new JButton("Cargar configuracion");
        JButton btnExportarDocx = new JButton("Exportar DOCX");
        JButton btnExportarCsv = new JButton("Exportar CSV");
        JButton btnExportarExcel = new JButton("Exportar Excel");

        btnEjecutar.addActionListener(e -> ejecutarSimulacion());
        btnEscenarioBase.addActionListener(e -> cargarEscenarioBase());
        btnLimpiar.addActionListener(e -> limpiarResultados());
        btnRestaurar.addActionListener(e -> restaurarValoresPorDefecto());
        btnGuardar.addActionListener(e -> guardarConfiguracion());
        btnCargar.addActionListener(e -> cargarConfiguracion());
        btnExportarDocx.addActionListener(e -> exportarDocx());
        btnExportarCsv.addActionListener(e -> exportarCsv());
        btnExportarExcel.addActionListener(e -> exportarExcel());

        estilizarBoton(btnEjecutar, AZUL_PETROLEO, Color.WHITE);
        estilizarBoton(btnEscenarioBase, AZUL_ACCION, Color.WHITE);
        estilizarBoton(btnLimpiar, new Color(103, 116, 132), Color.WHITE);
        estilizarBoton(btnRestaurar, GRIS_SUAVE, AZUL_PETROLEO);
        estilizarBoton(btnGuardar, GRIS_SUAVE, AZUL_PETROLEO);
        estilizarBoton(btnCargar, GRIS_SUAVE, AZUL_PETROLEO);
        estilizarBoton(btnExportarDocx, VERDE_ABASTECIMIENTO, Color.WHITE);
        estilizarBoton(btnExportarCsv, VERDE_ABASTECIMIENTO, Color.WHITE);
        estilizarBoton(btnExportarExcel, VERDE_ABASTECIMIENTO, Color.WHITE);

        botones.add(btnEjecutar);
        botones.add(btnEscenarioBase);
        botones.add(btnLimpiar);
        botones.add(btnRestaurar);
        botones.add(btnGuardar);
        botones.add(btnCargar);
        botones.add(btnExportarDocx);
        botones.add(btnExportarCsv);
        botones.add(btnExportarExcel);
        panel.add(botones, BorderLayout.EAST);
        return panel;
    }

    private void estilizarBoton(JButton boton, Color fondo, Color texto) {
        boton.setFont(FUENTE_BASE.deriveFont(Font.BOLD));
        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fondo.darker()),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        boton.setOpaque(true);
    }

    private JPanel crearPanelResultadosVehiculo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(GRIS_FONDO);
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
        panel.setBackground(GRIS_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(crearPanelTablaResumen("Resumen operativo", modeloResumenOperativo));
        panel.add(crearPanelTablaResumen("Resumen economico", modeloResumenEconomico));
        panel.add(crearPanelTablaResumen("Resumen de abastecimiento", modeloResumenAbastecimiento));
        panel.add(crearPanelTablaResumen("Utilizacion de surtidores", modeloUtilizacion));
        return panel;
    }

    private JPanel crearPanelTablaResumen(String titulo, DefaultTableModel modelo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BLANCO_TARJETA);
        panel.setBorder(crearBorde(titulo));

        JTable tabla = crearTabla(modelo, 22);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JTable crearTabla(DefaultTableModel modelo, int altoFila) {
        JTable tabla = new JTable(modelo) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 250, 252));
                    c.setForeground(new Color(31, 41, 55));
                }
                return c;
            }
        };
        tabla.setFont(FUENTE_BASE);
        tabla.getTableHeader().setFont(FUENTE_BASE.deriveFont(Font.BOLD));
        tabla.getTableHeader().setBackground(GRIS_SUAVE);
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.getTableHeader().setOpaque(true);
        tabla.getTableHeader().setDefaultRenderer(crearRenderEncabezado());
        tabla.setGridColor(new Color(226, 232, 240));
        tabla.setSelectionBackground(new Color(202, 231, 242));
        tabla.setSelectionForeground(new Color(20, 43, 60));
        tabla.setRowHeight(Math.max(altoFila, 25));
        tabla.setFillsViewportHeight(true);
        centrarColumnasNumericas(tabla);
        return tabla;
    }

    private DefaultTableCellRenderer crearRenderEncabezado() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(GRIS_SUAVE);
                c.setForeground(Color.BLACK);
                c.setFont(FUENTE_BASE.deriveFont(Font.BOLD));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GRIS_BORDE),
                        BorderFactory.createEmptyBorder(4, 6, 4, 6)
                ));
                return c;
            }
        };
    }

    private void centrarColumnasNumericas(JTable tabla) {
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            String nombre = tabla.getColumnName(i).toLowerCase(Locale.US);
            if (!nombre.contains("tipo") && !nombre.contains("carburante")
                    && !nombre.contains("ruta") && !nombre.contains("estado")
                    && !nombre.contains("motivo") && !nombre.contains("indicador")) {
                tabla.getColumnModel().getColumn(i).setCellRenderer(centrado);
            }
        }
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

            validarParametros(n, lambda, trimestre, inventarioInicial, capacidadMaxima, nivelMinimo,
                    tiempoCisternaMin, tiempoCisternaMax, cargaCisternaMin, cargaCisternaMax,
                    descargaMin, descargaMax);
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
            ultimosResultados = resultados;
            ultimoResumen = resumen;
            ultimoSimulador = simulador;

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
            int cantidadPeriodos,
            double inventarioInicial,
            double capacidadMaxima,
            double nivelMinimo,
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

        if (cantidadPeriodos < 0) {
            throw new IllegalArgumentException("La cantidad de periodos no puede ser negativa.");
        }

        if (inventarioInicial < 0) {
            throw new IllegalArgumentException("El inventario inicial no puede ser negativo.");
        }

        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad maxima debe ser mayor a 0.");
        }

        if (inventarioInicial > capacidadMaxima) {
            throw new IllegalArgumentException("El inventario inicial no debe superar la capacidad maxima.");
        }

        if (nivelMinimo < 0) {
            throw new IllegalArgumentException("El nivel minimo no puede ser negativo.");
        }

        if (nivelMinimo > capacidadMaxima) {
            throw new IllegalArgumentException("El nivel minimo no debe superar la capacidad maxima.");
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
        ultimosResultados = null;
        ultimoResumen = null;
        ultimoSimulador = null;
        panelGraficos.setResumen(null);
    }

    private void cargarEscenarioBase() {
        txtN.setText("50");
        txtLambda.setText("1.50");
        txtTrimestre.setText("4");
        cboPeriodicidad.setSelectedItem("Trimestral");
        txtSemilla.setText("12345");
        txtInventarioInicial.setText("3000");
        txtCapacidadMaxima.setText("30000");
        txtNivelMinimo.setText("3000");
        txtTiempoCisternaMin.setText("10");
        txtTiempoCisternaMax.setText("20");
        txtCargaCisternaMin.setText("15000");
        txtCargaCisternaMax.setText("25000");
        txtDescargaMin.setText("5");
        txtDescargaMax.setText("10");

        cargarTablaBasePrecios();
        cargarTablaBasePerfiles();
        limpiarResultados();
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

        cargarTablaBasePerfiles();
        cargarTablaBasePrecios();
    }

    private void cargarTablaBasePerfiles() {
        modeloPerfiles.setRowCount(0);
        modeloPerfiles.addRow(new Object[]{"Particular", "20", "45", "3", "6", "30"});
        modeloPerfiles.addRow(new Object[]{"Publico urbano", "25", "60", "4", "7", "60"});
        modeloPerfiles.addRow(new Object[]{"Interprovincial", "60", "150", "5", "9", "100"});
        modeloPerfiles.addRow(new Object[]{"Pesado", "200", "400", "7", "12", "150"});
    }

    private void cargarTablaBasePrecios() {
        modeloPrecios.setRowCount(0);
        modeloPrecios.addRow(new Object[]{"Gasolina", "6.96", "1.32", "8.68"});
        modeloPrecios.addRow(new Object[]{"Diesel", "9.80", "0.00", "9.80"});
        modeloPrecios.addRow(new Object[]{"GNV", "2.73", "0.00", "2.90"});
    }

    private void guardarConfiguracion() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar configuracion");
        chooser.setSelectedFile(new File("configuracion-simulador.properties"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = asegurarExtension(chooser.getSelectedFile(), ".properties");

        try {
            ConfiguracionArchivo.guardar(archivo, obtenerConfiguracion());
            JOptionPane.showMessageDialog(this, "Configuracion guardada correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la configuracion: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarConfiguracion() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar configuracion");

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            aplicarConfiguracion(ConfiguracionArchivo.cargar(chooser.getSelectedFile()));
            limpiarResultados();
            JOptionPane.showMessageDialog(this, "Configuracion cargada correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la configuracion: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarDocx() {
        if (ultimosResultados == null || ultimoResumen == null || ultimoSimulador == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay resultados para exportar.",
                    "Exportar DOCX",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar DOCX");
        chooser.setSelectedFile(new File("reporte-simulacion.docx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = asegurarExtension(chooser.getSelectedFile(), ".docx");

        try {
            ExportadorDocx.exportar(
                    archivo,
                    obtenerConfiguracion(),
                    ultimoResumen,
                    ultimoSimulador,
                    ultimosResultados
            );
            JOptionPane.showMessageDialog(this, "Reporte DOCX exportado correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el DOCX: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarCsv() {
        if (ultimosResultados == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay resultados para exportar.",
                    "Exportar CSV",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar CSV");
        chooser.setSelectedFile(new File("resultados-simulacion.csv"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = asegurarExtension(chooser.getSelectedFile(), ".csv");

        try {
            ExportadorCsv.exportar(archivo, ultimosResultados);
            JOptionPane.showMessageDialog(this, "Resultados CSV exportados correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el CSV: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarExcel() {
        if (ultimosResultados == null || ultimoResumen == null || ultimoSimulador == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay resultados para exportar.",
                    "Exportar Excel",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar Excel");
        chooser.setSelectedFile(new File("resultados-simulacion.xlsx"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = asegurarExtension(chooser.getSelectedFile(), ".xlsx");

        try {
            ExportadorExcel.exportar(
                    archivo,
                    obtenerConfiguracion(),
                    ultimoResumen,
                    ultimoSimulador,
                    ultimosResultados
            );
            JOptionPane.showMessageDialog(this, "Resultados Excel exportados correctamente.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo exportar el Excel: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private File asegurarExtension(File archivo, String extension) {
        if (archivo.getName().toLowerCase(Locale.US).endsWith(extension)) {
            return archivo;
        }
        return new File(archivo.getParentFile(), archivo.getName() + extension);
    }

    private Properties obtenerConfiguracion() {
        Properties props = new Properties();
        props.setProperty("general.n", txtN.getText());
        props.setProperty("general.lambda", txtLambda.getText());
        props.setProperty("general.periodos", txtTrimestre.getText());
        props.setProperty("general.periodicidad", String.valueOf(cboPeriodicidad.getSelectedItem()));
        props.setProperty("general.semilla", txtSemilla.getText());

        props.setProperty("abastecimiento.inventarioInicial", txtInventarioInicial.getText());
        props.setProperty("abastecimiento.capacidadMaxima", txtCapacidadMaxima.getText());
        props.setProperty("abastecimiento.nivelMinimo", txtNivelMinimo.getText());
        props.setProperty("abastecimiento.tiempoCisternaMin", txtTiempoCisternaMin.getText());
        props.setProperty("abastecimiento.tiempoCisternaMax", txtTiempoCisternaMax.getText());
        props.setProperty("abastecimiento.cargaCisternaMin", txtCargaCisternaMin.getText());
        props.setProperty("abastecimiento.cargaCisternaMax", txtCargaCisternaMax.getText());
        props.setProperty("abastecimiento.descargaMin", txtDescargaMin.getText());
        props.setProperty("abastecimiento.descargaMax", txtDescargaMax.getText());

        guardarTabla(props, "precio", modeloPrecios);
        guardarTabla(props, "perfil", modeloPerfiles);
        return props;
    }

    private void aplicarConfiguracion(Properties props) {
        txtN.setText(props.getProperty("general.n", "50"));
        txtLambda.setText(props.getProperty("general.lambda", "1.50"));
        txtTrimestre.setText(props.getProperty("general.periodos", "4"));
        cboPeriodicidad.setSelectedItem(props.getProperty("general.periodicidad", "Trimestral"));
        txtSemilla.setText(props.getProperty("general.semilla", "12345"));

        txtInventarioInicial.setText(props.getProperty("abastecimiento.inventarioInicial", "3000"));
        txtCapacidadMaxima.setText(props.getProperty("abastecimiento.capacidadMaxima", "30000"));
        txtNivelMinimo.setText(props.getProperty("abastecimiento.nivelMinimo", "3000"));
        txtTiempoCisternaMin.setText(props.getProperty("abastecimiento.tiempoCisternaMin", "10"));
        txtTiempoCisternaMax.setText(props.getProperty("abastecimiento.tiempoCisternaMax", "20"));
        txtCargaCisternaMin.setText(props.getProperty("abastecimiento.cargaCisternaMin", "15000"));
        txtCargaCisternaMax.setText(props.getProperty("abastecimiento.cargaCisternaMax", "25000"));
        txtDescargaMin.setText(props.getProperty("abastecimiento.descargaMin", "5"));
        txtDescargaMax.setText(props.getProperty("abastecimiento.descargaMax", "10"));

        cargarTabla(props, "precio", modeloPrecios);
        cargarTabla(props, "perfil", modeloPerfiles);
    }

    private void guardarTabla(Properties props, String prefijo, DefaultTableModel modelo) {
        props.setProperty(prefijo + ".filas", String.valueOf(modelo.getRowCount()));
        props.setProperty(prefijo + ".columnas", String.valueOf(modelo.getColumnCount()));

        for (int fila = 0; fila < modelo.getRowCount(); fila++) {
            for (int columna = 0; columna < modelo.getColumnCount(); columna++) {
                Object valor = modelo.getValueAt(fila, columna);
                props.setProperty(prefijo + "." + fila + "." + columna, valor == null ? "" : valor.toString());
            }
        }
    }

    private void cargarTabla(Properties props, String prefijo, DefaultTableModel modelo) {
        int filas = Integer.parseInt(props.getProperty(prefijo + ".filas", "0"));
        int columnas = modelo.getColumnCount();
        modelo.setRowCount(0);

        for (int fila = 0; fila < filas; fila++) {
            Object[] valores = new Object[columnas];
            for (int columna = 0; columna < columnas; columna++) {
                valores[columna] = props.getProperty(prefijo + "." + fila + "." + columna, "");
            }
            modelo.addRow(valores);
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
            UIManager.put("TableHeader.foreground", Color.BLACK);
            UIManager.put("TableHeader.background", GRIS_SUAVE);
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
