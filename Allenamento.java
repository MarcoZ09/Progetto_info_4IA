import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Allenamento extends JFrame {

    // 0 = vuoto, 1 = giocatore nero, 2 = bot bianco.
    private int[][] tavola = new int[8][8];
    private Cella[][] celle = new Cella[8][8];

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

    private Random casuale = new Random();
    private Timer timer;

    private boolean attesa = false;
    private boolean finita = false;

    private int suggeritaR = -1;
    private int suggeritaC = -1;
    private int pedineConsigliate;

    private Color oro = new Color(219, 188, 116);
    private Color sfondo = new Color(19, 38, 34);

    public Allenamento() {
        setTitle("Allenamento con Maestro Pedina");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel principale = new JPanel(new BorderLayout(0, 12));
        principale.setBackground(sfondo);
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
        JLabel guida = etichetta("Casella dorata = mossa consigliata");
        fondo.add(guida, BorderLayout.WEST);
        fondo.add(chiudi, BorderLayout.EAST);
        principale.add(fondo, BorderLayout.SOUTH);
        add(principale);

        // Posizione iniziale.
        tavola[3][3] = 2;
        tavola[4][4] = 2;
        tavola[3][4] = 1;
        tavola[4][3] = 1;

        consiglio(
                "Ciao! Sono Maestro Pedina. Tu giochi con il Nero. "
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
        info.setPreferredSize(new Dimension(202, 200));
        return info;
    }

    private JPanel pannelloMaestro() {
        JPanel maestro = new JPanel(new BorderLayout(0, 0));
        Color salvia = new Color(44, 71, 60);
        maestro.setBackground(salvia);
        maestro.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 4));
        maestro.setPreferredSize(new Dimension(530, 244));
        // Il viso è sopra il centro della GIF: il margine lo porta al centro del riquadro.
        mascotte.setOpaque(false);
        mascotte.setBorder(BorderFactory.createEmptyBorder(48, 0, 0, 0));
        mascotte.setPreferredSize(new Dimension(230, 196));
        maestro.add(mascotte, BorderLayout.EAST);

        JPanel nuvola = new JPanel(new BorderLayout(0, 6));
        nuvola.setOpaque(false);
        nuvola.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 30));
        JLabel nome = new JLabel("Maestro Pedina");
        nome.setFont(new Font("SansSerif", Font.BOLD, 16));
        nome.setForeground(oro);
        nuvola.add(nome, BorderLayout.NORTH);
        fumetto.setEditable(false);
        fumetto.setLineWrap(true);
        fumetto.setWrapStyleWord(true);
        fumetto.setFont(new Font("SansSerif", Font.PLAIN, 16));
        fumetto.setForeground(Stile.TESTO);
        fumetto.setBackground(salvia);
        JScrollPane testo = new JScrollPane(fumetto);
        testo.setBorder(null);
        testo.getViewport().setBackground(salvia);
        nuvola.add(testo, BorderLayout.CENTER);
        maestro.add(nuvola, BorderLayout.CENTER);
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
                celle[r][c] = new Cella(r, c);
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

    // Valutazione semplice della mossa del giocatore.
    private int punti(int r, int c) {
        int valore = catture(r, c, 1, false);

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
        String testo;

        if (angolo(r, c)) {
            testo = "Un angolo non puo' essere "
                    + "riconquistato dall'avversario.";
        } else if (rischio(r, c)) {
            testo = "Attenzione: vicino a un angolo vuoto "
                    + "rischi di facilitarne la conquista.";
        } else if (r == 0 || r == 7 || c == 0 || c == 7) {
            testo = "Il bordo puo' essere utile, "
                    + "ma non e' sicuro come un angolo.";
        } else {
            testo = "Qui non sei vicino a un angolo vuoto. "
                    + "Guarda anche le future risposte avversarie.";
        }

        return testo;
    }

    private String numeroPedine(int numero) {
        if (numero == 1) {
            return "1 pedina";
        }
        return numero + " pedine";
    }

    private void parla(String testo) {
        fumetto.setText(testo);
        fumetto.setCaretPosition(0);
    }

    private void consiglio(String inizio) {
        suggeritaR = -1;
        suggeritaC = -1;

        int migliore = -1000;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (catture(r, c, 1, false) > 0
                        && punti(r, c) > migliore) {
                    migliore = punti(r, c);
                    suggeritaR = r;
                    suggeritaC = c;
                }
            }
        }

        if (suggeritaR != -1) {
            pedineConsigliate = catture(suggeritaR, suggeritaC, 1, false);
            parla(
                    inizio
                    + "Ti consiglio "
                    + nome(suggeritaR, suggeritaC)
                    + ": qui catturi " + numeroPedine(pedineConsigliate) + ". "
                    + motivo(suggeritaR, suggeritaC)
            );
        }
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
        if (attesa || finita) {
            return;
        }

        int pedinePrese = catture(r, c, 1, false);
        if (pedinePrese == 0) {
            consiglio(
                    "Mossa non valida: devi chiudere almeno "
                    + "una pedina bianca tra due nere. "
            );
            return;
        }

        String commento = "Hai scelto " + nome(r, c) + ". ";

        // Confronta la tua cattura con il numero mostrato nel fumetto.
        consiglioSeguito = (r == suggeritaR && c == suggeritaC);
        if (consiglioSeguito) {
            mascotte.setIcon(felice);
            commento = "Hai seguito il consiglio! ";
        } else if (pedinePrese > pedineConsigliate) {
            mascotte.setIcon(occhiali);
        } else {
            mascotte.setIcon(neutra);
        }

        commento += "Hai catturato " + numeroPedine(pedinePrese) + ". ";
        if (!consiglioSeguito) {
            commento += "Il mio consiglio era da " + numeroPedine(pedineConsigliate) + ". ";
        }
        parla(commento + motivo(r, c) + " Ora tocca al bot...");

        catture(r, c, 1, true);
        tavola[r][c] = 1;

        suggeritaR = -1;
        suggeritaC = -1;

        prossimoTurno(2);
    }

    private void prossimoTurno(int colore) {
        // La partita finisce quando nessuno può muovere.
        if (!puoMuovere(1) && !puoMuovere(2)) {
            finita = true;
            attesa = false;
            aggiorna();

            int neri = conta(1);
            int bianchi = conta(2);
            String risultato = "Pareggio!";

            if (neri > bianchi) {
                risultato = "Hai vinto!";
            }

            if (bianchi > neri) {
                risultato = "Ha vinto il bot.";
            }

            parla(
                    risultato
                    + " Nero: " + neri
                    + ", Bianco: " + bianchi
                    + ". Ricorda: catturare tante pedine "
                    + "subito non basta per vincere."
            );
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
                parla(avviso);
            }

            // La pausa permette di leggere il commento.
            timer = new Timer(1800, e -> mossaBot());
            timer.setRepeats(false);
            timer.start();
        } else {
            consiglio(avviso);
        }

        aggiorna();
    }

    // Bot facile: sceglie casualmente una mossa valida.
    private void mossaBot() {
        if (finita || !isDisplayable()) {
            return;
        }

        int[][] mosse = new int[64][2];
        int numero = 0;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (catture(r, c, 2, false) > 0) {
                    mosse[numero][0] = r;
                    mosse[numero][1] = c;
                    numero++;
                }
            }
        }

        if (numero > 0) {
            int scelta = casuale.nextInt(numero);
            int r = mosse[scelta][0];
            int c = mosse[scelta][1];

            boolean botEraAvanti = conta(2) > conta(1);
            int pedinePerse = catture(r, c, 2, true);
            tavola[r][c] = 2;

            // Il sorpasso ha la precedenza sulla perdita di una pedina.
            if (!botEraAvanti && conta(2) > conta(1)) {
                mascotte.setIcon(triste);
            } else if (!consiglioSeguito && pedinePerse > 0) {
                mascotte.setIcon(arrabbiata);
            }
        }

        prossimoTurno(1);
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

        repaint();
    }

    @Override
    public void dispose() {
        if (timer != null) {
            timer.stop();
        }

        super.dispose();
    }

    // Casella che disegna la pedina e il suggerimento.
    private class Cella extends JButton {
        private int r;
        private int c;

        public Cella(int riga, int colonna) {
            r = riga;
            c = colonna;
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(new Color(58, 116, 87));
            g.fillRect(0, 0, getWidth(), getHeight());

            if (tavola[r][c] != 0) {
                if (tavola[r][c] == 1) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Stile.TESTO);
                }

                int d = Math.min(getWidth(), getHeight()) - 12;

                g.fillOval(
                        (getWidth() - d) / 2,
                        (getHeight() - d) / 2,
                        d, d
                );
            }

            if (r == suggeritaR && c == suggeritaC) {
                g.setColor(oro);
                g.drawRect(
                        2, 2, getWidth() - 5, getHeight() - 5
                );
                g.fillOval(
                        getWidth() / 2 - 5,
                        getHeight() / 2 - 5,
                        10, 10
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new Allenamento().setVisible(true)
        );
    }
}
