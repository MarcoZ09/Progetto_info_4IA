import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class MainMenu extends JFrame {

    private BufferedImage sfondo;

    // Immagine dello sfondo incorporata direttamente nel codice (Base64),
    // cosi' non serve nessun file esterno ne' percorsi da configurare.
    private static final String IMMAGINE_BASE64 =
        "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";

    public MainMenu() {
        setTitle("Othello - Menu Principale");
        setUndecorated(true); // toglie la barra del titolo per il fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // schermo intero
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Decodifica l'immagine dalla stringa Base64
        try {
            byte[] datiImmagine = Base64.getDecoder().decode(IMMAGINE_BASE64);
            sfondo = ImageIO.read(new ByteArrayInputStream(datiImmagine));
        } catch (IOException e) {
            System.err.println("Impossibile decodificare l'immagine di sfondo: " + e.getMessage());
        }

        Color nero = Color.BLACK;
        Color bianco = Color.WHITE;

        // Pannello personalizzato che disegna l'immagine come sfondo
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (sfondo != null) {
                    g.drawImage(sfondo, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new GridLayout(4, 1, 20, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(100, 300, 100, 300));

        JLabel titolo = new JLabel("OTHELLO", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 60));
        titolo.setForeground(bianco);

        JButton giocaButton = new MinecraftButton("Gioca");
        JButton impostazioniButton = new MinecraftButton("Impostazioni");
        JButton esciButton = new MinecraftButton("Esci dal gioco");

        for (JButton b : new JButton[]{giocaButton, impostazioniButton, esciButton}) {
            b.setPreferredSize(new Dimension(360, 64));
        }

        // Azione: Gioca (apre la scelta della modalita')
        giocaButton.addActionListener(e -> {
            dispose(); // chiude il menu
            ModalitaGioco scelta = new ModalitaGioco();
            scelta.setVisible(true);
        });

        // Azione: Impostazioni
        impostazioniButton.addActionListener(e -> {
            System.out.println("Hai scelto: Impostazioni");
            // Qui in futuro andra' il codice per aprire la schermata delle impostazioni
        });

        // Azione: Esci dal gioco
        esciButton.addActionListener(e -> {
            System.exit(0);
        });

        panel.add(titolo);
        panel.add(giocaButton);
        panel.add(impostazioniButton);
        panel.add(esciButton);

        add(panel);
    }

    public static void main(String[] args) {
        // Avvia l'interfaccia grafica in modo sicuro (thread di Swing)
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true); // "display" della finestra
        });
    }
}