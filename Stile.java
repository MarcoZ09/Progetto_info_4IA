import javax.swing.*;
import java.awt.*;

// Colori e piccoli metodi comuni alle schermate. Solo Swing e AWT.
public class Stile {
    public static final Color SFONDO = new Color(19, 38, 34);
    public static final Color PANNELLO = new Color(29, 54, 47);
    public static final Color TESTO = new Color(242, 239, 222);
    public static final Color ORO = new Color(219, 188, 116);

    public static void preparaFinestra(JFrame finestra) {
        finestra.setSize(960, 780);
        finestra.setMinimumSize(new Dimension(800, 780));
        finestra.setLocationRelativeTo(null);
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void cambiaSchermata(JFrame attuale, JFrame nuova) {
        nuova.setBounds(attuale.getBounds());
        nuova.setExtendedState(attuale.getExtendedState());
        nuova.setVisible(true);
        attuale.dispose();
    }

    public static JLabel etichetta(String testo, int dimensione, Color colore) {
        JLabel label = new JLabel(testo, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, dimensione));
        label.setForeground(colore);
        return label;
    }

    // GridBagLayout senza vincoli mantiene il pannello al centro della finestra.
    public static JPanel centra(JPanel contenuto) {
        JPanel sfondo = new JPanel(new GridBagLayout());
        sfondo.setBackground(SFONDO);
        sfondo.add(contenuto);
        return sfondo;
    }
}

