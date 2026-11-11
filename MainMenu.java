import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Othello - Menu principale");
        Stile.preparaFinestra(this);

        JPanel panel = new JPanel(new BorderLayout(0, 28));
        panel.setBackground(Stile.PANNELLO);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        panel.setPreferredSize(new Dimension(570, 550));

        JPanel titoli = new JPanel(new GridLayout(3, 1, 0, 4));
        titoli.setOpaque(false);
        titoli.add(Stile.etichetta("IL CLASSICO GIOCO DI STRATEGIA", 14, Stile.ORO));
        titoli.add(Stile.etichetta("OTHELLO", 58, Stile.TESTO));
        titoli.add(Stile.etichetta("Due colori. Una nuova sfida.", 17, Stile.TESTO));
        panel.add(titoli, BorderLayout.NORTH);

        JPanel pulsanti = new JPanel(new GridLayout(3, 1, 0, 14));
        pulsanti.setOpaque(false);
        JButton giocaButton = new MinecraftButton("Gioca");
        JButton impostazioniButton = new MinecraftButton("Impostazioni");
        JButton esciButton = new MinecraftButton("Esci dal gioco");
        // Azione: Gioca (apre la scelta della modalita')
        giocaButton.addActionListener(e -> {
            Stile.cambiaSchermata(this, new ModalitaGioco());
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

        pulsanti.add(giocaButton);
        pulsanti.add(impostazioniButton);
        pulsanti.add(esciButton);
        panel.add(pulsanti, BorderLayout.CENTER);
        panel.add(Stile.etichetta("8 x 8  /  NERO E BIANCO", 14, Stile.ORO), BorderLayout.SOUTH);
        add(Stile.centra(panel));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
