// GraficoResultadosPanel.java
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Map;

public class GraficoResultadosPanel extends JPanel {

    private ResumenSimulacion resumen;

    public void setResumen(ResumenSimulacion resumen) {
        this.resumen = resumen;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (resumen == null) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Ejecute una simulacion para ver las graficas.", 40, 60);
            return;
        }

        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Graficas representativas del simulador de surtidores", 30, 35);

        dibujarGraficoDemanda(g, 40, 70);
        dibujarGraficoUtilizacion(g, 40, 280);
    }

    private void dibujarGraficoDemanda(Graphics g, int x, int y) {
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Distribucion de demanda por ruta", x, y);

        int baseY = y + 140;
        int altoMax = 110;
        int ancho = 80;

        double sub = resumen.getPorcentajeSubvencionado();
        double intl = resumen.getPorcentajeInternacional();

        int altoSub = (int) (altoMax * sub / 100.0);
        int altoInt = (int) (altoMax * intl / 100.0);

        g.setColor(new Color(30, 120, 180));
        g.fillRect(x + 40, baseY - altoSub, ancho, altoSub);

        g.setColor(new Color(220, 120, 40));
        g.fillRect(x + 170, baseY - altoInt, ancho, altoInt);

        g.setColor(Color.BLACK);
        g.drawRect(x + 40, baseY - altoSub, ancho, altoSub);
        g.drawRect(x + 170, baseY - altoInt, ancho, altoInt);

        g.drawString(String.format("%.2f%%", sub), x + 48, baseY - altoSub - 8);
        g.drawString(String.format("%.2f%%", intl), x + 178, baseY - altoInt - 8);

        g.drawString("Subv.", x + 55, baseY + 20);
        g.drawString("Internac.", x + 175, baseY + 20);
    }

    private void dibujarGraficoUtilizacion(Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Utilizacion de surtidores", x, y);

        int baseY = y + 160;
        int altoMax = 130;
        int ancho = 55;
        int espacio = 75;
        int i = 0;

        for (Map.Entry<String, Double> entry : resumen.getUtilizacionPorSurtidor().entrySet()) {
            String surtidor = entry.getKey();
            double valor = entry.getValue();

            if (valor > 100.0) {
                valor = 100.0;
            }

            int alto = (int) (altoMax * valor / 100.0);
            int barraX = x + 30 + i * espacio;

            g.setColor(new Color(80, 150, 90));
            g.fillRect(barraX, baseY - alto, ancho, alto);

            g.setColor(Color.BLACK);
            g.drawRect(barraX, baseY - alto, ancho, alto);
            g.drawString(String.format("%.1f%%", valor), barraX - 2, baseY - alto - 8);
            g.drawString(surtidor, barraX + 15, baseY + 20);

            i++;
        }

        g.drawString("Lectura: valores cercanos a 100% indican saturacion del recurso.", x, baseY + 55);
    }
}
