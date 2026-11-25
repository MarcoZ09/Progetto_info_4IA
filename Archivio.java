import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

class Archivio {
    private static final Path CARTELLA = Paths.get(System.getProperty("user.home"), "Othello4IA");
    private static final Path OPZIONI = CARTELLA.resolve("impostazioni.txt");
    private static final Path PARTITE = CARTELLA.resolve("partite.txt");
    static boolean animaFumetto = true;
    static boolean testoGrande = false;

    static void caricaImpostazioni(Component finestra) {
        if (!Files.exists(OPZIONI)) return;
        try {
            List<String> righe = Files.readAllLines(OPZIONI, StandardCharsets.UTF_8);
            animaFumetto = !righe.contains("fumetto=fermo");
            testoGrande = righe.contains("testo=grande");
        } catch (IOException e) {
            avvisa(finestra, "Impossibile leggere le impostazioni.");
        }
    }

    static void impostazioni(Component finestra) {
        JCheckBox anima = new JCheckBox("Anima il fumetto", animaFumetto);
        JCheckBox grande = new JCheckBox("Usa scritte più grandi", testoGrande);
        JPanel pannello = new JPanel(new GridLayout(0, 1));
        pannello.add(new JLabel("Per i prossimi allenamenti"));
        pannello.add(anima);
        pannello.add(grande);
        if (JOptionPane.showConfirmDialog(finestra, pannello, "Impostazioni",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;

        String effetto = anima.isSelected() ? "animato" : "fermo";
        String testo = grande.isSelected() ? "grande" : "normale";
        try {
            Files.createDirectories(CARTELLA);
            Files.write(OPZIONI, java.util.Arrays.asList("fumetto=" + effetto, "testo=" + testo),
                    StandardCharsets.UTF_8);
            animaFumetto = anima.isSelected();
            testoGrande = grande.isSelected();
        } catch (IOException e) {
            avvisa(finestra, "Impossibile salvare le impostazioni.");
        }
    }

    static String risultato(int nero, int bianco, boolean controComputer) {
        String esito = "Pareggio!";
        if (nero > bianco) esito = controComputer ? "Hai vinto!" : "Vince il Nero!";
        if (bianco > nero) esito = controComputer ? "Hai perso. Vince il Bianco." : "Vince il Bianco!";
        return esito + "   Nero " + nero + " · Bianco " + bianco;
    }

    static void registra(Component finestra, String modalita, String risultato) {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        String riga = data + "   |   " + modalita + "   |   " + risultato;
        try {
            Files.createDirectories(CARTELLA);
            Files.write(PARTITE, Collections.singletonList(riga), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            avvisa(finestra, "Impossibile salvare la partita in cronologia.");
        }
    }

    static void cronologia(Component finestra) {
        try {
            if (Files.exists(PARTITE)) {
                List<String> partite = Files.readAllLines(PARTITE, StandardCharsets.UTF_8);
                if (!partite.isEmpty()) {
                    Collections.reverse(partite);
                    JTextArea testo = new JTextArea(String.join("\n", partite), 12, 58);
                    testo.setEditable(false);
                    testo.setCaretPosition(0);
                    JOptionPane.showMessageDialog(finestra, new JScrollPane(testo),
                            "Cronologia · più recenti per prime", JOptionPane.PLAIN_MESSAGE);
                    return;
                }
            }
            JOptionPane.showMessageDialog(finestra, "Qui troverai i risultati delle partite concluse.",
                    "Cronologia", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            avvisa(finestra, "Impossibile leggere la cronologia.");
        }
    }

    private static void avvisa(Component finestra, String testo) {
        JOptionPane.showMessageDialog(finestra, testo, "Archivio Othello", JOptionPane.WARNING_MESSAGE);
    }
}
