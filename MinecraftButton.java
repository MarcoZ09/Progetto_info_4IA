import javax.swing.*;
import java.awt.*;

// Il nome originale rimane uguale per non cambiare i collegamenti del progetto.
public class MinecraftButton extends JButton {
    public MinecraftButton(String text) {
        super(text);
        setFont(new Font("SansSerif", Font.BOLD, 20));
        setForeground(Stile.TESTO);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setMargin(new Insets(12, 20, 12, 20));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(420, 58));
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Il colore cambia usando semplici condizioni.
        Color colore = new Color(48, 83, 68);
        if (getModel().isPressed()) {
            colore = new Color(33, 62, 50);
        } else if (getModel().isRollover()) {
            colore = new Color(65, 105, 85);
        }
        g.setColor(colore);
        g.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
        g.setColor(new Color(100, 137, 108));
        if (hasFocus()) {
            g.setColor(Stile.ORO);
        }
        g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
        if (hasFocus()) {
            g.drawRoundRect(3, 3, getWidth() - 7, getHeight() - 7, 14, 14);
        }
        super.paintComponent(g);
    }
}
