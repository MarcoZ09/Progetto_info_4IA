import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

// Il colore logico e' sempre quello di destinazione. La faccia visibile
// cambia soltanto quando il disco passa di taglio.
public class Casella extends JButton {
    private static final Color VERDE = new Color(51, 116, 85);
    private static final Color VERDE_CHIARO = new Color(58, 125, 92);
    private static final Color GRAFITE = new Color(62, 72, 69);
    private static final Color CARBONE = new Color(17, 23, 22);
    private static final Color AVORIO = new Color(255, 252, 234);
    private static final Color PERLA = new Color(206, 205, 183);

    private int pedina, precedente, ritardo;
    private double progresso = 1;
    private boolean suggerita;

    public Casella(int riga, int colonna) {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setBackground((riga + colonna) % 2 == 0 ? VERDE_CHIARO : VERDE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        getAccessibleContext().setAccessibleName("" + (char) ('A' + colonna) + (riga + 1));
        mostra(0);
    }

    public void mostra(int colore) {
        precedente = pedina;
        pedina = colore;
        progresso = 1;
        getAccessibleContext().setAccessibleDescription(
                colore == 0 ? "Vuota" : colore == 1 ? "Pedina nera" : "Pedina bianca");
        repaint();
    }

    boolean prepara(int colore, int distanza) {
        if (pedina == colore) return false;
        mostra(colore);
        ritardo = distanza == 0 ? 0 : 95 + (distanza - 1) * 43;
        progresso = 0;
        return true;
    }

    boolean avanza(long millisecondi) {
        int durata = precedente == 0 ? 220 : 310;
        progresso = Math.max(0, Math.min(1, (millisecondi - ritardo) / (double) durata));
        repaint();
        return progresso < 1;
    }

    public void suggerisci(boolean valore) {
        if (suggerita != valore) {
            suggerita = valore;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D penna = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();
        penna.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        penna.setColor(getBackground());
        penna.fillRect(0, 0, w, h);
        if (getModel().isRollover()) {
            penna.setColor(new Color(239, 241, 199, 26));
            penna.fillRect(0, 0, w, h);
        }
        if (pedina != 0) {
            Graphics2D disco = (Graphics2D) penna.create();
            disegnaPedina(disco);
            disco.dispose();
        }
        if (hasFocus() || suggerita) {
            penna.setColor(Stile.ORO);
            penna.drawRoundRect(2, 2, w - 5, h - 5, 5, 5);
            if (suggerita) penna.fillOval(w / 2 - 4, h / 2 - 4, 8, 8);
        }
        penna.dispose();
    }

    private void disegnaPedina(Graphics2D g) {
        double d = Math.min(getWidth(), getHeight()) * 0.78;
        if (d < 2) return;
        double t = progresso * progresso * (3 - 2 * progresso);
        double scala = 1, altezza = 1, salto = 0;
        int faccia = pedina;
        if (precedente == 0 && progresso < 1) {
            scala = 0.72 + 0.28 * t;
            salto = d * (0.19 * (1 - t) + 0.04 * Math.sin(2 * Math.PI * t));
            g.setComposite(AlphaComposite.SrcOver.derive((float) t));
        } else if (progresso < 1) {
            double angolo = Math.PI * t;
            altezza = Math.abs(Math.cos(angolo));
            salto = d * 0.13 * Math.sin(angolo);
            faccia = t < 0.5 ? precedente : pedina;
        }

        g.translate(getWidth() / 2.0, getHeight() / 2.0);
        g.setColor(new Color(4, 24, 18, 80));
        double ombra = d * (0.91 - salto / d);
        g.fill(new Ellipse2D.Double(-ombra / 2, d * 0.23, ombra, d * 0.32));
        g.translate(0, -salto);
        g.scale(scala, scala);
        boolean nera = faccia == 1;
        double h = d * altezza;
        double spessore = d * 0.095;
        g.setColor(nera ? CARBONE : new Color(151, 151, 133));
        g.fill(new Ellipse2D.Double(-d / 2, -(h + spessore) / 2, d, h + spessore));
        if (h < 1) return;
        g.translate(0, -spessore / 2);
        g.scale(1, altezza);
        float r = (float) (d / 2);
        g.setPaint(new GradientPaint(-r, -r, nera ? GRAFITE : AVORIO,
                r, r, nera ? CARBONE : PERLA));
        g.fill(new Ellipse2D.Double(-r, -r, d, d));
        g.setColor(nera ? new Color(240, 249, 235, 42) : new Color(122, 121, 99, 76));
        g.draw(new Ellipse2D.Double(-r * 0.85, -r * 0.85, d * 0.85, d * 0.85));
    }
}
