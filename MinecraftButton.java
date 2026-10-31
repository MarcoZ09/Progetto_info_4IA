import javax.swing.*;
import java.awt.*;

/**
 * Pulsante in stile Minecraft GUI: superfici pixelate, bordo in rilievo
 * e variazioni di luminosita' al passaggio del mouse e alla pressione.
 */
public class MinecraftButton extends JButton {
    private static final Color BASE = new Color(90, 90, 90);
    private static final Color HOVER = new Color(120, 120, 120);
    private static final Color PRESSED = new Color(55, 55, 55);
    private static final Color LIGHT = new Color(190, 190, 190);
    private static final Color DARK = new Color(35, 35, 35);

    public MinecraftButton(String text) {
        super(text);
        setFont(new Font("Monospaced", Font.BOLD, 22));
        setForeground(Color.WHITE);
        setHorizontalAlignment(SwingConstants.CENTER);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setMargin(new Insets(10, 18, 10, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        int w = getWidth();
        int h = getHeight();

        Color fill = getModel().isPressed() ? PRESSED
                : getModel().isRollover() ? HOVER : BASE;
        g.setColor(fill);
        g.fillRect(2, 2, w - 4, h - 4);

        // Bordo squadrato con luce in alto/sinistra e ombra in basso/destra.
        g.setColor(LIGHT);
        g.fillRect(0, 0, w, 3);
        g.fillRect(0, 0, 3, h);
        g.setColor(DARK);
        g.fillRect(0, h - 3, w, 3);
        g.fillRect(w - 3, 0, 3, h);

        // Linea interna sottile per richiamare la texture della GUI.
        g.setColor(new Color(255, 255, 255, 35));
        g.drawRect(4, 4, Math.max(0, w - 9), Math.max(0, h - 9));
        g.dispose();

        super.paintComponent(graphics);
    }
}
