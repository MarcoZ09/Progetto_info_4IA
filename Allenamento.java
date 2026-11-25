import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public class Allenamento extends JFrame {

    // 0 = vuoto, 1 = giocatore nero, 2 = bot bianco.
    private int[][] tavola = new int[8][8];
    private final Casella[][] celle = new Casella[8][8];
    private final AnimazioneMossa animazione = new AnimazioneMossa(celle);

    private JTextArea fumetto = new JTextArea();
    private JLabel stato = new JLabel();
    private JLabel puntiNero = new JLabel("", SwingConstants.CENTER);
    private JLabel puntiBianco = new JLabel("", SwingConstants.CENTER);

    private ImageIcon felice = caricaGif("Happy");
    private ImageIcon arrabbiata = caricaGif("Angry");
    private ImageIcon neutra = caricaGif("Neutral");
    private ImageIcon occhiali = caricaGif("Occhiali "); // Il file ha uno spazio prima di .gif
    private ImageIcon triste = caricaGif("Sad");
    private JLabel mascotte = new JLabel(neutra, SwingConstants.CENTER);
    private boolean consiglioSeguito = false;

    private ImageIcon caricaGif(String nome) {
        ImageIcon gif = new ImageIcon(Allenamento.class.getResource("/Immagini/Othellity-" + nome + ".gif"));
        int lato = gif.getIconWidth() * 5 / 2;
        return new ImageIcon(gif.getImage().getScaledInstance(lato, lato, Image.SCALE_REPLICATE));
    }

    private JPanel spazioNuvola = new JPanel(new BorderLayout());
    private Timer scrittura = new Timer(45, e -> avanzaFumetto());
    private String frase;
    private int lettereVisibili;
    private boolean animaFumetto;
    private String ultimaMossaBot = "";
    private Timer timer;

    private boolean attesa = false;
    private boolean finita = false;

    private int suggeritaR = -1;
    private int suggeritaC = -1;
    private int pedineConsigliate;

    private Color oro = new Color(219, 188, 116);
    private Color sfondo = new Color(19, 38, 34);

    public Allenamento() {
        Archivio.caricaImpostazioni(this);
        animaFumetto = Archivio.animaFumetto;
        setTitle("Allenamento con Othellity");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principale = new Sfondo(new BorderLayout(0, 12));
        principale.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JPanel fascia = new JPanel(new BorderLayout(16, 0));
        fascia.setOpaque(false);
        fascia.add(pannelloPunteggio(), BorderLayout.WEST);
        fascia.add(pannelloMaestro(), BorderLayout.CENTER);
        principale.add(fascia, BorderLayout.NORTH);
        principale.add(pannelloTavola(), BorderLayout.CENTER);

        JButton chiudi = new JButton("Chiudi allenamento");
        chiudi.addActionListener(e -> dispose());
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setOpaque(false);
        JLabel guida = etichetta("Il bordo dorato indica il consiglio di Othellity.");
        fondo.add(guida, BorderLayout.WEST);
        fondo.add(chiudi, BorderLayout.EAST);
        principale.add(fondo, BorderLayout.SOUTH);
        add(principale);

        // Posizione iniziale.
        tavola[3][3] = 2;
        tavola[4][4] = 2;
        tavola[3][4] = 1;
        tavola[4][3] = 1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) celle[r][c].mostra(tavola[r][c]);
        }

        consiglio(
                "Tu giochi con il Nero. "
        );
        aggiorna();

        setSize(1024, 780);
        setMinimumSize(new Dimension(760, 590));
        setLocationRelativeTo(null);
    }

    private JPanel pannelloPunteggio() {
        JPanel info = new JPanel(new GridLayout(3, 1, 0, 6));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(22, 2, 22, 6));
        JLabel titolo = new JLabel("Allenamento");
        titolo.setFont(new Font("SansSerif", Font.BOLD, 25));
        titolo.setForeground(Stile.TESTO);
        stato.setFont(new Font("SansSerif", Font.BOLD, 16));
        stato.setForeground(oro);
        info.add(titolo);
        info.add(stato);

        JPanel punti = new JPanel(new GridLayout(1, 2, 5, 0));
        punti.setOpaque(false);
        puntiNero.setOpaque(true);
        puntiNero.setBackground(new Color(30, 40, 37));
        puntiNero.setForeground(Stile.TESTO);
        puntiBianco.setOpaque(true);
        puntiBianco.setBackground(Stile.TESTO);
        puntiBianco.setForeground(sfondo);
        punti.add(puntiNero);
        punti.add(puntiBianco);
        info.add(punti);
        info.setPreferredSize(new Dimension(202, 196));
        return info;
    }

    private JPanel pannelloMaestro() {
        JPanel maestro = new JPanel(new BorderLayout(4, 0));
        maestro.setOpaque(false);
        maestro.setPreferredSize(new Dimension(530, 196));

        JPanel ritratto = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D penna = (Graphics2D) g.create();
                penna.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int y = (getHeight() - 102) / 2;
                penna.setColor(new Color(58, 85, 67));
                penna.fillRoundRect(0, y, 102, 102, 24, 24);
                penna.setColor(new Color(90, 117, 88));
                penna.drawRoundRect(0, y, 101, 101, 24, 24);
                penna.dispose();
            }
        };
        ritratto.setOpaque(false);
        ritratto.setPreferredSize(new Dimension(102, 102));
        // Centra la parte visibile del personaggio nel fotogramma trasparente.
        mascotte.setBorder(BorderFactory.createEmptyBorder(46, 0, 0, 0));
        ritratto.add(mascotte);
        maestro.add(ritratto, BorderLayout.WEST);

        JPanel nuvola = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D penna = (Graphics2D) g.create();
                penna.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int centro = getHeight() / 2;
                Area forma = new Area(new RoundRectangle2D.Double(
                        12, 2, getWidth() - 13, getHeight() - 7, 24, 24));
                Path2D punta = new Path2D.Double();
                punta.moveTo(14, centro - 10);
                punta.lineTo(0, centro);
                punta.lineTo(14, centro + 10);
                punta.closePath();
                forma.add(new Area(punta));
                penna.translate(0, 3);
                penna.setColor(new Color(0, 0, 0, 60));
                penna.fill(forma);
                penna.translate(0, -3);
                penna.setColor(new Color(250, 248, 240));
                penna.fill(forma);
                penna.dispose();
            }
        };
        nuvola.setOpaque(false);
        nuvola.setBorder(BorderFactory.createEmptyBorder(16, 30, 17, 17));
        JLabel nome = new JLabel("Othellity  ·  Il tuo allenatore");
        nome.setFont(new Font("SansSerif", Font.BOLD, 16));
        nome.setForeground(new Color(64, 98, 51));
        nuvola.add(nome, BorderLayout.NORTH);
        fumetto.setEditable(false);
        fumetto.setLineWrap(true);
        fumetto.setWrapStyleWord(true);
        fumetto.setFont(new Font("SansSerif", Font.PLAIN, Archivio.testoGrande ? 21 : 17));
        fumetto.setForeground(new Color(42, 47, 40));
        fumetto.setOpaque(false);
        JScrollPane testo = new JScrollPane(fumetto);
        testo.setBorder(null);
        testo.setOpaque(false);
        testo.getViewport().setOpaque(false);
        testo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        nuvola.add(testo, BorderLayout.CENTER);
        spazioNuvola.setOpaque(false);
        spazioNuvola.add(nuvola, BorderLayout.CENTER);
        maestro.add(spazioNuvola, BorderLayout.CENTER);
        return maestro;
    }

    private JPanel pannelloTavola() {
        JPanel griglia = new JPanel(new GridLayout(8, 8, 2, 2));
        griglia.setBackground(new Color(31, 60, 49));
        griglia.setBorder(BorderFactory.createLineBorder(new Color(80, 108, 85), 2));
        JPanel lettere = new JPanel(new GridLayout(1, 8));
        lettere.setOpaque(false);
        lettere.setBorder(BorderFactory.createEmptyBorder(0, 24, 4, 0));
        JPanel numeri = new JPanel(new GridLayout(8, 1));
        numeri.setOpaque(false);
        numeri.setPreferredSize(new Dimension(24, 1));
        for (int c = 0; c < 8; c++) {
            lettere.add(etichetta("" + (char) ('A' + c)));
        }
        for (int r = 0; r < 8; r++) {
            numeri.add(etichetta("" + (r + 1)));
            for (int c = 0; c < 8; c++) {
                celle[r][c] = new Casella(r, c);
                final int riga = r;
                final int colonna = c;
                celle[r][c].addActionListener(e -> gioca(riga, colonna));
                griglia.add(celle[r][c]);
            }
        }
        JPanel tavolaConCoordinate = new JPanel(new BorderLayout());
        tavolaConCoordinate.setOpaque(false);
        lettere.setPreferredSize(new Dimension(1, 24));
        tavolaConCoordinate.add(lettere, BorderLayout.NORTH);
        tavolaConCoordinate.add(numeri, BorderLayout.WEST);
        tavolaConCoordinate.add(griglia, BorderLayout.CENTER);

        // Usa il lato piu' corto: la scacchiera rimane quadrata anche a schermo intero.
        JPanel spazio = new JPanel(null) {
            public void doLayout() {
                int lato = Math.min(getWidth(), getHeight());
                tavolaConCoordinate.setBounds((getWidth() - lato) / 2,
                        (getHeight() - lato) / 2, lato, lato);
            }
        };
        spazio.setOpaque(false);
        spazio.add(tavolaConCoordinate);
        return spazio;
    }

    private JLabel etichetta(String testo) {
        JLabel label = new JLabel(testo, SwingConstants.CENTER);
        label.setForeground(oro);
        return label;
    }

    private boolean dentro(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    /*
     * Se applica è false, conta soltanto le catture.
     * Se applica è true, gira anche le pedine.
     * La nuova pedina viene posizionata dopo questo metodo.
     */
    private int catture(
            int riga, int colonna, int colore, boolean applica
    ) {
        if (tavola[riga][colonna] != 0) {
            return 0;
        }

        int totale = 0;

        // Controlla tutte le otto direzioni.
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }

                int r = riga + dr;
                int c = colonna + dc;
                int numero = 0;

                while (dentro(r, c)
                        && tavola[r][c] == 3 - colore) {
                    numero++;
                    r += dr;
                    c += dc;
                }

                if (dentro(r, c) && tavola[r][c] == colore) {
                    totale += numero;

                    if (applica) {
                        for (int i = 1; i <= numero; i++) {
                            tavola[riga + dr * i]
                                  [colonna + dc * i] = colore;
                        }
                    }
                }
            }
        }

        return totale;
    }

    private boolean angolo(int r, int c) {
        return (r == 0 || r == 7) && (c == 0 || c == 7);
    }

    // Controlla se la casella è vicina a un angolo vuoto.
    private boolean rischio(int r, int c) {
        for (int a = 0; a <= 7; a += 7) {
            for (int b = 0; b <= 7; b += 7) {
                if (!angolo(r, c)
                        && tavola[a][b] == 0
                        && Math.abs(r - a) <= 1
                        && Math.abs(c - b) <= 1) {
                    return true;
                }
            }
        }

        return false;
    }

    // Lo stesso criterio guida il bot bianco e i consigli per il Nero.
    private int punti(int r, int c, int pedineGirate) {
        int valore = pedineGirate;

        if (angolo(r, c)) {
            valore += 100;
        } else if (rischio(r, c)) {
            valore -= 50;
        } else if (r == 0 || r == 7 || c == 0 || c == 7) {
            valore += 10;
        }

        return valore;
    }

    private String nome(int r, int c) {
        return "" + (char) ('A' + c) + (r + 1);
    }

    private String motivo(int r, int c) {
        if (angolo(r, c)) return "Una pedina nell’angolo è tua fino alla fine.";
        if (rischio(r, c)) return "Occhio all’angolo vuoto vicino. Il Bianco potrebbe conquistarlo.";
        if (r == 0 || r == 7 || c == 0 || c == 7) return "Sul bordo puoi essere attaccato da meno direzioni.";
        return "Con questa mossa giri pedine all’interno della scacchiera.";
    }

    private String numeroPedine(int numero) {
        if (numero == 1) {
            return "1 pedina";
        }
        return numero + " pedine";
    }

    private void parla(String testo) {
        scrittura.stop();
        frase = testo;
        lettereVisibili = 0;
        if (animaFumetto) {
            avanzaFumetto();
            if (lettereVisibili < frase.length()) scrittura.start();
        } else {
            fumetto.setText(frase);
        }
        fumetto.setCaretPosition(0);
    }

    // A ogni scatto compare una parola e la nuvola entra di pochi pixel.
    private void avanzaFumetto() {
        int spazio = frase.indexOf(' ', lettereVisibili);
        lettereVisibili = spazio < 0 ? frase.length() : spazio + 1;
        int margine = Math.max(0, 16 - lettereVisibili);
        spazioNuvola.setBorder(BorderFactory.createEmptyBorder(margine, 0, 0, 0));
        fumetto.setText(frase.substring(0, lettereVisibili));
        fumetto.setCaretPosition(0);
        if (lettereVisibili == frase.length()) scrittura.stop();
    }

    private Move mossaMigliore(int colore) {
        Move scelta = null;
        int migliore = Integer.MIN_VALUE;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                int girate = catture(r, c, colore, false);
                if (girate == 0) continue;
                int valore = punti(r, c, girate);
                if (valore > migliore) {
                    migliore = valore;
                    scelta = new Move(r, c);
                }
            }
        }
        return scelta;
    }

    private void consiglio(String inizio) {
        Move scelta = mossaMigliore(1);
        if (scelta == null) return;
        suggeritaR = scelta.getRow();
        suggeritaC = scelta.getCol();
        pedineConsigliate = catture(suggeritaR, suggeritaC, 1, false);
        parla(inizio + "Prova " + nome(suggeritaR, suggeritaC) + " per girare "
                + numeroPedine(pedineConsigliate) + ". " + motivo(suggeritaR, suggeritaC));
    }

    private boolean puoMuovere(int colore) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (catture(r, c, colore, false) > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private void gioca(int r, int c) {
        if (attesa || finita || animazione.inCorso()) {
            return;
        }

        int pedinePrese = catture(r, c, 1, false);
        if (pedinePrese == 0) {
            mascotte.setIcon(neutra);
            String aiuto = "Da qui non racchiudi nessuna pedina bianca. ";
            if (tavola[r][c] != 0) aiuto = nome(r, c) + " è già occupata. ";
            consiglio(aiuto);
            return;
        }

        consiglioSeguito = (r == suggeritaR && c == suggeritaC);
        String commento = "Hai giocato in ";
        if (consiglioSeguito) {
            mascotte.setIcon(felice);
            commento = "Hai seguito il consiglio in ";
        } else if (pedinePrese > pedineConsigliate) {
            mascotte.setIcon(occhiali);
            commento = "Hai girato più pedine del previsto! Hai giocato in ";
        } else {
            mascotte.setIcon(neutra);
        }
        parla(commento + nome(r, c) + " e girato " + numeroPedine(pedinePrese) + ". " + motivo(r, c));
        attesa = true;
        catture(r, c, 1, true);
        tavola[r][c] = 1;

        suggeritaR = -1;
        suggeritaC = -1;

        aggiorna();
        animazione.avvia(tavola, r, c, () -> prossimoTurno(2));
    }

    private void prossimoTurno(int colore) {
        // La partita finisce quando nessuno può muovere.
        if (!puoMuovere(1) && !puoMuovere(2)) {
            finita = true;
            attesa = false;
            aggiorna();

            int neri = conta(1);
            int bianchi = conta(2);
            String risultato = Archivio.risultato(neri, bianchi, true);
            if (neri > bianchi) mascotte.setIcon(felice);
            else if (neri < bianchi) mascotte.setIcon(triste);
            else mascotte.setIcon(neutra);
            parla(risultato);
            Archivio.registra(this, "Allenamento", risultato);
            return;
        }

        String avviso = "";

        // Se non ci sono mosse, il turno passa all'altro.
        if (!puoMuovere(colore)) {
            if (colore == 1) {
                avviso = "Non hai mosse: il bot gioca ancora. ";
            } else {
                avviso = "Il bot passa: giochi ancora tu. ";
            }

            colore = 3 - colore;
        }

        attesa = colore == 2;

        if (attesa) {
            if (!avviso.isEmpty()) {
                parla(ultimaMossaBot + avviso);
            }

            // La pausa permette di leggere il commento.
            timer = new Timer(1800, e -> mossaBot());
            timer.setRepeats(false);
            timer.start();
        } else {
            consiglio(ultimaMossaBot + avviso);
            ultimaMossaBot = "";
        }

        aggiorna();
    }

    private void mossaBot() {
        if (finita || !isDisplayable()) return;
        Move scelta = mossaMigliore(2);
        if (scelta == null) {
            prossimoTurno(1);
            return;
        }
        int r = scelta.getRow();
        int c = scelta.getCol();
        boolean biancoEraAvanti = conta(2) > conta(1);
        int girate = catture(r, c, 2, true);
        tavola[r][c] = 2;
        ultimaMossaBot = "Il Bianco ha giocato in " + nome(r, c) + " e girato "
                + numeroPedine(girate) + ". ";
        if (!biancoEraAvanti && conta(2) > conta(1)) {
            mascotte.setIcon(triste);
            ultimaMossaBot += "Ora il Bianco è in vantaggio. ";
        } else if (!consiglioSeguito && girate > 0) {
            mascotte.setIcon(arrabbiata);
        }
        aggiorna();
        stato.setText("Mossa del Bianco");
        animazione.avvia(tavola, r, c, () -> prossimoTurno(1));
    }

    private int conta(int colore) {
        int numero = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (tavola[r][c] == colore) {
                    numero++;
                }
            }
        }

        return numero;
    }

    private void aggiorna() {
        String turno;

        if (attesa) {
            turno = "Il bot sta pensando";
        } else {
            turno = "Tocca a te (Nero)";
        }

        if (finita) {
            turno = "Partita terminata";
        }

        stato.setText(turno);
        puntiNero.setText("Nero  " + conta(1));
        puntiBianco.setText("Bianco  " + conta(2));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                celle[r][c].suggerisci(r == suggeritaR && c == suggeritaC);
            }
        }
    }

    @Override
    public void dispose() {
        scrittura.stop();
        animazione.ferma();
        if (timer != null) {
            timer.stop();
        }

        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new Allenamento().setVisible(true)
        );
    }
}
