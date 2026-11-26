import javax.swing.*;
import java.awt.*;

public class ModalitaGioco extends JFrame {

    public ModalitaGioco() {
        setTitle("Othello - Scegli la modalita'");
        Stile.preparaFinestra(this);

        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setBackground(Stile.PANNELLO);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));
        panel.setPreferredSize(new Dimension(620, 600));

        JPanel titoli = new JPanel(new GridLayout(2, 1, 0, 12));
        titoli.setOpaque(false);
        titoli.add(Stile.etichetta("SCEGLI LA MODALITA'", 30, Stile.TESTO));
        titoli.add(Stile.etichetta(
                "Sfida il computer, un amico oppure allenati", 17, Stile.ORO));
        panel.add(titoli, BorderLayout.NORTH);

        JPanel pulsanti = new JPanel(new GridLayout(4, 1, 0, 16));
        pulsanti.setOpaque(false);

        JButton iaButton = new MinecraftButton("Gioca contro l'IA");
        JButton multiButton = new MinecraftButton("Multigiocatore (2 giocatori)");
        JButton allenamentoButton = new MinecraftButton("Allenamento");
        JButton indietroButton = new MinecraftButton("Indietro");

        JButton[] elencoPulsanti = {iaButton, multiButton, allenamentoButton, indietroButton};
        for (JButton pulsante : elencoPulsanti) {
            pulsante.setPreferredSize(new Dimension(420, 64));
        }

        // Apre una schermata grafica dedicata alla scelta della difficolta'.
        iaButton.addActionListener(e -> scegliDifficolta());

        multiButton.addActionListener(e -> Stile.cambiaSchermata(this, new OthelloGame(false)));

        allenamentoButton.addActionListener(e -> {
            Allenamento lezione = new Allenamento();
            lezione.setBounds(getBounds());
            lezione.setExtendedState(getExtendedState());
            lezione.setVisible(true);
        });

        indietroButton.addActionListener(e -> Stile.cambiaSchermata(this, new MainMenu()));

        pulsanti.add(iaButton);
        pulsanti.add(multiButton);
        pulsanti.add(allenamentoButton);
        pulsanti.add(indietroButton);

        panel.add(pulsanti, BorderLayout.CENTER);
        panel.add(Stile.etichetta("Il Nero gioca per primo", 15, Stile.ORO), BorderLayout.SOUTH);
        add(Stile.centra(panel));
    }

    private void scegliDifficolta() {
        JDialog dialog = new JDialog(this, "Difficolta' dell'IA", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel sfondo = new JPanel(new BorderLayout(0, 18));
        sfondo.setBackground(Stile.PANNELLO);
        sfondo.setBorder(BorderFactory.createEmptyBorder(28, 30, 25, 30));

        JPanel intestazione = new JPanel(new GridLayout(2, 1, 0, 8));
        intestazione.setOpaque(false);
        intestazione.add(Stile.etichetta("SCEGLI LA SFIDA", 28, Stile.TESTO));
        intestazione.add(Stile.etichetta("Quanto vuoi mettere alla prova la tua strategia?", 16, Stile.ORO));
        sfondo.add(intestazione, BorderLayout.NORTH);

        JPanel scelte = new JPanel(new GridLayout(3, 1, 0, 13));
        scelte.setOpaque(false);

        String[] nomi = {"FACILE", "MEDIO", "DIFFICILE"};
        String[] descrizioni = {
            "L'IA gioca in modo semplice: ideale per imparare.",
            "Una sfida equilibrata: pianifica le tue mosse.",
            "L'IA sfrutta le strategie: preparati a una sfida!"
        };
        String[] codici = {"easy", "medium", "hard"};

        for (int i = 0; i < nomi.length; i++) {
            final String codice = codici[i];
            JPanel riga = new JPanel(new BorderLayout(12, 5));
            riga.setBackground(new Color(36, 55, 43));
            riga.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Stile.ORO, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)));

            JPanel testo = new JPanel(new GridLayout(2, 1, 0, 5));
            testo.setOpaque(false);
            testo.add(Stile.etichetta(nomi[i], 20, Stile.TESTO));
            testo.add(Stile.etichetta(descrizioni[i], 13, Stile.ORO));

            JButton scegli = new MinecraftButton("SCEGLI");
            scegli.setPreferredSize(new Dimension(115, 48));
            scegli.addActionListener(e -> {
                dialog.dispose();
                Stile.cambiaSchermata(this, new OthelloGame(true, codice));
            });

            riga.add(testo, BorderLayout.CENTER);
            riga.add(scegli, BorderLayout.EAST);
            scelte.add(riga);
        }
        sfondo.add(scelte, BorderLayout.CENTER);

        JButton annulla = new MinecraftButton("Annulla");
        annulla.addActionListener(e -> dialog.dispose());
        JPanel fondo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fondo.setOpaque(false);
        fondo.add(annulla);
        sfondo.add(fondo, BorderLayout.SOUTH);

        dialog.setContentPane(sfondo);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(560, 390));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
