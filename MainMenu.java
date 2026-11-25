import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Othello - Menu principale");
        Stile.preparaFinestra(this);

        // Carica lo sfondo prima dalle risorse del progetto e, se non presente,
        // prova i normali percorsi delle cartelle del progetto.
        Image immagineSfondo = caricaSfondo();

        JPanel sfondoPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (immagineSfondo != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    double scala = Math.max(
                            (double) getWidth() / immagineSfondo.getWidth(null),
                            (double) getHeight() / immagineSfondo.getHeight(null));
                    int w = (int) (immagineSfondo.getWidth(null) * scala);
                    int h = (int) (immagineSfondo.getHeight(null) * scala);
                    g2.drawImage(immagineSfondo, (getWidth() - w) / 2,
                            (getHeight() - h) / 2, w, h, this);
                    g2.setColor(new Color(0, 0, 0, 45));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                } else {
                    g.setColor(Stile.SFONDO);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        sfondoPanel.setOpaque(true);

        JPanel panel = new JPanel(new BorderLayout(0, 28));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        panel.setPreferredSize(new Dimension(570, 550));

        JPanel titoli = new JPanel(new GridLayout(3, 1, 0, 4));
        titoli.setOpaque(false);
        titoli.add(Stile.etichetta("IL CLASSICO GIOCO DI STRATEGIA", 14, Stile.ORO));
        titoli.add(Stile.etichetta("OTHELLO", 58, Stile.TESTO));
        titoli.add(Stile.etichetta("Due colori. Una nuova sfida.", 17, Stile.TESTO));
        panel.add(titoli, BorderLayout.NORTH);

        JPanel pulsanti = new JPanel(new GridLayout(4, 1, 0, 12));
        pulsanti.setOpaque(false);
        JButton giocaButton = new MinecraftButton("Gioca");
        JButton impostazioniButton = new MinecraftButton("Impostazioni");
        JButton esciButton = new MinecraftButton("Esci dal gioco");
        // Azione: Gioca (apre la scelta della modalita')
        giocaButton.addActionListener(e -> {
            Stile.cambiaSchermata(this, new ModalitaGioco());
        });

        Archivio.caricaImpostazioni(this);
        impostazioniButton.addActionListener(e -> Archivio.impostazioni(this));
        JButton cronologiaButton = new MinecraftButton("Cronologia partite");
        cronologiaButton.addActionListener(e -> Archivio.cronologia(this));

        // Azione: Esci dal gioco
        esciButton.addActionListener(e -> {
            System.exit(0);
        });

        pulsanti.add(giocaButton);
        pulsanti.add(impostazioniButton);
        pulsanti.add(cronologiaButton);
        pulsanti.add(esciButton);
        panel.add(pulsanti, BorderLayout.CENTER);
        panel.add(Stile.etichetta("8 x 8  /  NERO E BIANCO", 14, Stile.ORO), BorderLayout.SOUTH);
        JPanel contenitore = new JPanel(new GridBagLayout());
        contenitore.setOpaque(false);
        contenitore.add(panel);
        sfondoPanel.add(contenitore);
        setContentPane(sfondoPanel);
    }

    private Image caricaSfondo() {
        String nomeFile = "SfondoOthello.png";

        // Percorso standard per risorse incluse nel classpath.
        URL risorsa = MainMenu.class.getResource("/" + nomeFile);
        if (risorsa != null) {
            return new ImageIcon(risorsa).getImage();
        }

        // Percorsi alternativi per progetti avviati direttamente dall'IDE.
        String[] percorsi = {
            nomeFile,
            "Immagini/" + nomeFile,
            "src/Immagini/" + nomeFile,
            "src/main/resources/Immagini/" + nomeFile
        };

        for (String percorso : percorsi) {
            File file = new File(percorso);
            if (file.isFile()) {
                return new ImageIcon(file.getAbsolutePath()).getImage();
            }
        }

        System.err.println("Sfondo non trovato. Inserire " + nomeFile
                + " nella cartella Immagini del progetto.");
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
