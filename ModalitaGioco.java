import javax.swing.*;
import java.awt.*;

public class ModalitaGioco extends JFrame {

    public ModalitaGioco() {
        setTitle("Othello - Scegli la modalita'");
        Stile.preparaFinestra(this);

        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setBackground(Stile.PANNELLO);
        panel.setBorder(
                BorderFactory.createEmptyBorder(35, 40, 35, 40)
        );
        panel.setPreferredSize(new Dimension(620, 600));

        JPanel titoli = new JPanel(new GridLayout(2, 1, 0, 12));
        titoli.setOpaque(false);
        titoli.add(
                Stile.etichetta("SCEGLI LA MODALITA'", 30, Stile.TESTO)
        );
        titoli.add(
                Stile.etichetta(
                        "Sfida il computer, un amico oppure allenati",
                        17,
                        Stile.ORO
                )
        );
        panel.add(titoli, BorderLayout.NORTH);

        // Quattro righe per i quattro pulsanti.
        JPanel pulsanti = new JPanel(new GridLayout(4, 1, 0, 16));
        pulsanti.setOpaque(false);

        JButton iaButton = new MinecraftButton("Gioca contro l'IA");
        JButton multiButton =
                new MinecraftButton("Multigiocatore (2 giocatori)");
        JButton allenamentoButton =
                new MinecraftButton("Allenamento");
        JButton indietroButton = new MinecraftButton("Indietro");

        JButton[] elencoPulsanti = {
                iaButton,
                multiButton,
                allenamentoButton,
                indietroButton
        };

        for (JButton pulsante : elencoPulsanti) {
            pulsante.setPreferredSize(new Dimension(420, 64));
        }

        // Partita contro l'IA.
        iaButton.addActionListener(e -> {
            String[] livelli = {"Facile", "Medio", "Difficile"};
            String[] codici = {"easy", "medium", "hard"};

            int scelta = JOptionPane.showOptionDialog(
                    this,
                    "Scegli la difficolta' dell'IA",
                    "Difficolta'",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    livelli,
                    livelli[1]
            );

            // Se chiudi la scelta, rimani nel menu.
            if (scelta < 0) {
                return;
            }

            Stile.cambiaSchermata(this, new OthelloGame(true, codici[scelta]));
        });

        // Partita a due giocatori sullo stesso computer.
        multiButton.addActionListener(e -> {
            Stile.cambiaSchermata(this, new OthelloGame(false));
        });

        // Il menu rimane aperto dietro la finestra di allenamento.
        allenamentoButton.addActionListener(e -> {
            Allenamento lezione = new Allenamento();
            lezione.setBounds(getBounds());
            lezione.setExtendedState(getExtendedState());
            lezione.setVisible(true);
        });

        // Torna al menu principale.
        indietroButton.addActionListener(e -> {
            Stile.cambiaSchermata(this, new MainMenu());
        });

        pulsanti.add(iaButton);
        pulsanti.add(multiButton);
        pulsanti.add(allenamentoButton);
        pulsanti.add(indietroButton);

        panel.add(pulsanti, BorderLayout.CENTER);
        panel.add(
                Stile.etichetta("Il Nero gioca per primo", 15, Stile.ORO),
                BorderLayout.SOUTH
        );

        add(Stile.centra(panel));
    }
}