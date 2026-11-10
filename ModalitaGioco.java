import javax.swing.*;
import java.awt.*;

public class ModalitaGioco extends JFrame {

    public ModalitaGioco() {
        setTitle("Othello - Scegli la modalita'");
        Stile.preparaFinestra(this);

        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setBackground(Stile.PANNELLO);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));
        panel.setPreferredSize(new Dimension(620, 510));

        JPanel titoli = new JPanel(new GridLayout(2, 1, 0, 12));
        titoli.setOpaque(false);
        titoli.add(Stile.etichetta("SCEGLI LA MODALITA'", 30, Stile.TESTO));
        titoli.add(Stile.etichetta("Sfida il computer oppure un amico", 17, Stile.ORO));
        panel.add(titoli, BorderLayout.NORTH);
        JPanel pulsanti = new JPanel(new GridLayout(3, 1, 0, 16));
        pulsanti.setOpaque(false);

        JButton iaButton = new MinecraftButton("Gioca contro l'IA");
        JButton multiButton = new MinecraftButton("Multigiocatore (2 giocatori)");
        JButton indietroButton = new MinecraftButton("Indietro");

        for (JButton b : new JButton[]{iaButton, multiButton, indietroButton}) {
            b.setPreferredSize(new Dimension(420, 64));
        }

        // Partita contro l'IA
        iaButton.addActionListener(e -> {
            // Chiede la difficolta' dell'IA (usata da DiffAI)
            String[] livelli = {"Facile", "Medio", "Difficile"};
            String[] codici = {"easy", "medium", "hard"};
            int scelta = JOptionPane.showOptionDialog(this,
                    "Scegli la difficolta' dell'IA", "Difficolta'",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, livelli, livelli[1]);
            if (scelta < 0) return; // finestra chiusa: si resta su questa schermata

            dispose();
            new OthelloGame(true, codici[scelta]).setVisible(true);
        });

        // Partita a due giocatori sullo stesso computer
        multiButton.addActionListener(e -> {
            dispose();
            new OthelloGame(false).setVisible(true);
        });

        // Torna al menu principale
        indietroButton.addActionListener(e -> {
            dispose();
            new MainMenu().setVisible(true);
        });

        pulsanti.add(iaButton);
        pulsanti.add(multiButton);
        pulsanti.add(indietroButton);

        panel.add(pulsanti, BorderLayout.CENTER);
        panel.add(Stile.etichetta("Il Nero gioca per primo", 15, Stile.ORO), BorderLayout.SOUTH);
        add(Stile.centra(panel));
    }
}