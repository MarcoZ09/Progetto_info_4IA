import javax.swing.*;
import java.awt.*;

// Una casella e' un normale JButton che disegna la pedina con un cerchio.
public class Casella extends JButton {
    public Casella() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(new Color(51, 115, 83));
        if (getModel().isRollover()) {
            g.setColor(new Color(68, 133, 98));
        }
        g.fillRect(0, 0, getWidth(), getHeight());

        // Il testo originale indica se la casella contiene una pedina.
        if (!getText().equals("")) {
            int diametro = Math.min(getWidth(), getHeight()) - 14;
            int x = (getWidth() - diametro) / 2;
            int y = (getHeight() - diametro) / 2;
            g.setColor(new Color(30, 75, 53));
            g.fillOval(x + 2, y + 3, diametro, diametro);
            if (getForeground().equals(Color.BLACK)) {
                g.setColor(new Color(27, 31, 30));
            } else {
                g.setColor(new Color(245, 242, 227));
            }
            g.fillOval(x, y, diametro, diametro);
            if (getForeground().equals(Color.BLACK)) {
                g.setColor(new Color(83, 91, 86));
            } else {
                g.setColor(new Color(201, 198, 182));
            }
            g.drawOval(x + 3, y + 3, diametro - 6, diametro - 6);
        }
        if (hasFocus()) {
            g.setColor(Stile.ORO);
            g.drawRect(3, 3, getWidth() - 7, getHeight() - 7);
        }
    }
}
