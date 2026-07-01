// GraficoResultadosPanel.java
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Map;

public class GraficoResultadosPanel extends JPanel {

    private static final Color FONDO = new Color(241, 244, 248);
    private static final Color TARJETA = Color.WHITE;
    private static final Color BORDE = new Color(207, 216, 226);
    private static final Color TEXTO = new Color(32, 41, 55);
    private static final Color TEXTO_SUAVE = new Color(95, 107, 124);
    private static final Color SUBVENCIONADO = new Color(22, 84, 112);
    private static final Color INTERNACIONAL = new Color(221, 132, 48);
    private static final Color NO_ATENDIDO = new Color(184, 82, 82);
    private static final Color VERDE = new Color(56, 142, 93);
    private static final Color MORADO = new Color(126, 96, 191);
    private static final Font FUENTE = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FUENTE_VALOR = new Font("SansSerif", Font.BOLD, 20);

    private ResumenSimulacion resumen;

    public GraficoResultadosPanel() {
        setBackground(FONDO);
        setPreferredSize(new Dimension(1120, 720));
    }

    public void setResumen(ResumenSimulacion resumen) {
        this.resumen = resumen;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(FUENTE);

        int ancho = getWidth();
        int alto = getHeight();
        int margen = 18;

        if (resumen == null) {
            dibujarVacio(g2, ancho, alto);
            g2.dispose();
            return;
        }

        dibujarTarjetas(g2, margen, margen, ancho - 2 * margen);

        int yGraficos = margen + 112;
        int espacio = 14;
        int anchoGrafico = Math.max(300, (ancho - 2 * margen - espacio) / 2);
        int altoGrafico = Math.max(235, (alto - yGraficos - margen - espacio) / 2);

        dibujarPanel(g2, margen, yGraficos, anchoGrafico, altoGrafico, "Distribucion de demanda por ruta");
        dibujarDemanda(g2, margen, yGraficos, anchoGrafico, altoGrafico);

        dibujarPanel(g2, margen + anchoGrafico + espacio, yGraficos, anchoGrafico, altoGrafico, "Utilizacion de surtidores");
        dibujarUtilizacion(g2, margen + anchoGrafico + espacio, yGraficos, anchoGrafico, altoGrafico);

        int yInferior = yGraficos + altoGrafico + espacio;
        dibujarPanel(g2, margen, yInferior, anchoGrafico, altoGrafico, "Ingresos por ruta");
        dibujarIngresos(g2, margen, yInferior, anchoGrafico, altoGrafico);

        dibujarPanel(g2, margen + anchoGrafico + espacio, yInferior, anchoGrafico, altoGrafico, "Inventario y abastecimiento");
        dibujarInventario(g2, margen + anchoGrafico + espacio, yInferior, anchoGrafico, altoGrafico);

        g2.dispose();
    }

    private void dibujarVacio(Graphics2D g2, int ancho, int alto) {
        int w = Math.min(520, ancho - 80);
        int h = 130;
        int x = (ancho - w) / 2;
        int y = Math.max(50, (alto - h) / 2);
        tarjeta(g2, x, y, w, h);
        g2.setColor(TEXTO);
        g2.setFont(FUENTE_TITULO);
        centrar(g2, "Ejecute una simulacion para ver las graficas", x, y + 48, w);
        g2.setColor(TEXTO_SUAVE);
        g2.setFont(FUENTE);
        centrar(g2, "Aqui se mostraran demanda, utilizacion, ingresos e inventario.", x, y + 78, w);
    }

    private void dibujarTarjetas(Graphics2D g2, int x, int y, int ancho) {
        int espacio = 12;
        int tarjetaW = (ancho - 3 * espacio) / 4;
        dibujarTarjeta(g2, x, y, tarjetaW, "Vehiculos simulados", String.valueOf(resumen.getVehiculosSimulados()), SUBVENCIONADO);
        dibujarTarjeta(g2, x + tarjetaW + espacio, y, tarjetaW, "Espera promedio", f(resumen.getEsperaPromedio()) + " min", VERDE);
        dibujarTarjeta(g2, x + 2 * (tarjetaW + espacio), y, tarjetaW, "Ingreso total", "Bs. " + f(resumen.getIngresoTotal()), INTERNACIONAL);
        dibujarTarjeta(g2, x + 3 * (tarjetaW + espacio), y, tarjetaW, "Inventario final", f(resumen.getInventarioFinal()) + " L", MORADO);
    }

    private void dibujarTarjeta(Graphics2D g2, int x, int y, int w, String titulo, String valor, Color color) {
        tarjeta(g2, x, y, w, 88);
        g2.setColor(color);
        g2.fillRoundRect(x + 14, y + 18, 8, 50, 8, 8);
        g2.setColor(TEXTO_SUAVE);
        g2.setFont(FUENTE);
        g2.drawString(titulo, x + 34, y + 30);
        g2.setColor(TEXTO);
        g2.setFont(FUENTE_VALOR);
        g2.drawString(valor, x + 34, y + 60);
    }

    private void dibujarPanel(Graphics2D g2, int x, int y, int w, int h, String titulo) {
        tarjeta(g2, x, y, w, h);
        g2.setColor(new Color(233, 241, 245));
        g2.fillRoundRect(x + 10, y + 8, w - 20, 28, 10, 10);
        g2.setColor(TEXTO);
        g2.setFont(FUENTE_TITULO);
        g2.drawString(titulo, x + 16, y + 26);
    }

    private void dibujarDemanda(Graphics2D g2, int x, int y, int w, int h) {
        int plotX = x + 46;
        int plotY = y + 52;
        int plotW = w - 82;
        int plotH = h - 92;
        double sub = resumen.getCantidadSubvencionada();
        double intl = resumen.getCantidadInternacional();
        double noAtendidos = resumen.getVehiculosNoAtendidos();
        double max = max(1, sub, intl, noAtendidos);
        String[] labels = {"Subv.", "Internac.", "No atend."};
        double[] valores = {sub, intl, noAtendidos};
        Color[] colores = {SUBVENCIONADO, INTERNACIONAL, NO_ATENDIDO};
        dibujarBarras(g2, plotX, plotY, plotW, plotH, labels, valores, colores, max, false);
    }

    private void dibujarUtilizacion(Graphics2D g2, int x, int y, int w, int h) {
        int plotX = x + 40;
        int plotY = y + 52;
        int plotW = w - 72;
        int plotH = h - 92;
        int n = resumen.getUtilizacionPorSurtidor().size();
        String[] labels = new String[n];
        double[] valores = new double[n];
        Color[] colores = new Color[n];
        int i = 0;
        for (Map.Entry<String, Double> entry : resumen.getUtilizacionPorSurtidor().entrySet()) {
            labels[i] = entry.getKey();
            valores[i] = Math.min(100.0, Math.max(0.0, entry.getValue()));
            colores[i] = VERDE;
            i++;
        }
        dibujarBarras(g2, plotX, plotY, plotW, plotH, labels, valores, colores, 100.0, true);
    }

    private void dibujarIngresos(Graphics2D g2, int x, int y, int w, int h) {
        int plotX = x + 46;
        int plotY = y + 52;
        int plotW = w - 82;
        int plotH = h - 92;
        double sub = resumen.getIngresoSubvencionado();
        double intl = resumen.getIngresoInternacional();
        double max = max(1, sub, intl);
        dibujarBarras(g2, plotX, plotY, plotW, plotH,
                new String[]{"Subv.", "Internac."},
                new double[]{sub, intl},
                new Color[]{SUBVENCIONADO, INTERNACIONAL},
                max,
                false);
    }

    private void dibujarInventario(Graphics2D g2, int x, int y, int w, int h) {
        int plotX = x + 42;
        int plotY = y + 52;
        int plotW = w - 78;
        int plotH = h - 92;
        double inicial = resumen.getInventarioInicial();
        double abastecido = resumen.getLitrosAbastecidosReales();
        double vendido = resumen.getLitrosVendidosTotales();
        double finalInv = resumen.getInventarioFinal();
        double max = max(1, inicial, abastecido, vendido, finalInv);
        dibujarBarras(g2, plotX, plotY, plotW, plotH,
                new String[]{"Inicial", "Abast.", "Vendido", "Final"},
                new double[]{inicial, abastecido, vendido, finalInv},
                new Color[]{MORADO, VERDE, INTERNACIONAL, SUBVENCIONADO},
                max,
                false);
    }

    private void dibujarBarras(
            Graphics2D g2,
            int x,
            int y,
            int w,
            int h,
            String[] labels,
            double[] valores,
            Color[] colores,
            double max,
            boolean porcentaje
    ) {
        int ejeY = y + h;
        g2.setColor(new Color(226, 232, 240));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(x, y, x, ejeY);
        g2.drawLine(x, ejeY, x + w, ejeY);

        int n = Math.max(1, labels.length);
        int espacio = Math.max(16, w / (n * 5));
        int anchoBarra = Math.max(28, (w - espacio * (n + 1)) / n);
        FontMetrics fm = g2.getFontMetrics(FUENTE);

        for (int i = 0; i < labels.length; i++) {
            double valor = Math.max(0.0, valores[i]);
            int alto = max <= 0 ? 0 : (int) Math.round((valor / max) * (h - 24));
            int bx = x + espacio + i * (anchoBarra + espacio);
            int by = ejeY - alto;

            g2.setColor(colores[i]);
            g2.fillRoundRect(bx, by, anchoBarra, alto, 8, 8);
            g2.setColor(colores[i].darker());
            g2.drawRoundRect(bx, by, anchoBarra, alto, 8, 8);

            String valorTxt = porcentaje ? f(valor) + "%" : abreviar(valor);
            g2.setColor(TEXTO);
            g2.setFont(FUENTE);
            g2.drawString(valorTxt, bx + (anchoBarra - fm.stringWidth(valorTxt)) / 2, Math.max(y + 14, by - 6));

            String label = labels[i];
            g2.setColor(TEXTO_SUAVE);
            g2.drawString(label, bx + (anchoBarra - fm.stringWidth(label)) / 2, ejeY + 18);
        }
    }

    private void tarjeta(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(TARJETA);
        g2.fillRoundRect(x, y, w, h, 12, 12);
        g2.setColor(BORDE);
        g2.drawRoundRect(x, y, w, h, 12, 12);
    }

    private void centrar(Graphics2D g2, String texto, int x, int y, int w) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, x + (w - fm.stringWidth(texto)) / 2, y);
    }

    private double max(double minimo, double... valores) {
        double max = minimo;
        for (double valor : valores) {
            if (valor > max) {
                max = valor;
            }
        }
        return max;
    }

    private String f(double valor) {
        return String.format("%.2f", valor);
    }

    private String abreviar(double valor) {
        if (valor >= 1_000_000) {
            return String.format("%.1fM", valor / 1_000_000.0);
        }
        if (valor >= 10_000) {
            return String.format("%.1fk", valor / 1_000.0);
        }
        return String.format("%.0f", valor);
    }
}
