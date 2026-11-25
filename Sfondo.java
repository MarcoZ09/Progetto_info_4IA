import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

// Il motivo viene ridisegnato solo quando cambiano le dimensioni del pannello.
class Sfondo extends JPanel {
    private BufferedImage fondale;

    Sfondo(LayoutManager layout) {
        super(layout);
        setBackground(Stile.SFONDO);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;
        if (fondale == null || fondale.getWidth() != w || fondale.getHeight() != h) {
            fondale = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D p = fondale.createGraphics();
            p.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            p.setPaint(new RadialGradientPaint(new Point2D.Float(w * 0.36f, h * 0.35f),
                    Math.max(w, h) * 0.85f, new float[]{0, 1},
                    new Color[]{new Color(47, 82, 65), new Color(12, 31, 28)}));
            p.fillRect(0, 0, w, h);
            // Trama minuta del panno, con due grandi dischi incisi ai bordi.
            p.setColor(new Color(240, 235, 204, 12));
            for (int y = 0; y < h; y += 6) {
                for (int x = y % 12 == 0 ? 0 : 3; x < w; x += 6) p.fillRect(x, y, 1, 1);
            }
            int raggio = Math.min(w, h) / 3;
            incisione(p, w - raggio / 4, -raggio / 5, raggio);
            incisione(p, raggio / 7, h + raggio / 5, raggio);
            p.dispose();
        }
        g.drawImage(fondale, 0, 0, null);
    }

    private void incisione(Graphics2D g, int x, int y, int r) {
        Graphics2D p = (Graphics2D) g.create();
        p.translate(x, y);
        p.setColor(new Color(3, 23, 20, 38));
        p.fillOval(-r, -r, r * 2, r * 2);
        p.setColor(new Color(216, 189, 123, 67));
        for (double scala : new double[]{1, 0.97, 0.77, 0.75}) {
            int a = (int) (r * scala);
            p.drawOval(-a, -a, a * 2, a * 2);
        }
        for (int i = 0; i < 64; i++) {
            p.drawLine(0, -r * 96 / 100, 0, -r * (i % 8 == 0 ? 89 : 94) / 100);
            p.rotate(Math.PI / 32);
        }
        p.dispose();
    }
}
