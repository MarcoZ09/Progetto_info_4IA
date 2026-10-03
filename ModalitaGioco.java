import javax.swing.*;
import java.awt.*;

public class ModalitaGioco extends JFrame {

    public ModalitaGioco() {
        setTitle("Othello - Scegli la modalita'");
        setUndecorated(true); // schermo intero senza barra del titolo
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Stessi colori del menu principale (tema Othello)
        Color verdeTavolo = new Color(0, 100, 0);
        Color nero = Color.BLACK;
        Color bianco = Color.WHITE;

        JPanel panel = new JPanel(new GridLayout(4, 1, 20, 20));
        panel.setBackground(verdeTavolo);
        panel.setBorder(BorderFactory.createEmptyBorder(100, 300, 100, 300));

        JLabel titolo = new JLabel("SCEGLI LA MODALITA'", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 50));
        titolo.setForeground(bianco);

        JButton iaButton = new JButton("Gioca contro l'IA");
        JButton multiButton = new JButton("Multigiocatore (2 giocatori)");
        JButton indietroButton = new JButton("Indietro");

        for (JButton b : new JButton[]{iaButton, multiButton, indietroButton}) {
            b.setFont(new Font("Arial", Font.BOLD, 28));
            b.setBackground(nero);
            b.setForeground(bianco);
            b.setFocusPainted(false);
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

        panel.add(titolo);
        panel.add(iaButton);
        panel.add(multiButton);
        panel.add(indietroButton);

        add(panel);
    }
}