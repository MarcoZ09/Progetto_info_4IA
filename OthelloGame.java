import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class OthelloGame extends JFrame {

    private static final int DIMENSIONE = 8;
    private static final int VUOTO = 0;
    private static final int NERO = 1;   // il giocatore umano
    private static final int BIANCO = 2; // l'IA

    // Direzioni delle 8 caselle vicine (per cercare pezzi da catturare)
    private static final int[] DX = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] DY = {-1, 0, 1, -1, 1, -1, 0, 1};

    private int[][] tavola = new int[DIMENSIONE][DIMENSIONE];
    private JButton[][] celle = new JButton[DIMENSIONE][DIMENSIONE];
    private int turno = NERO; // il nero inizia sempre, come nelle regole ufficiali
    private JLabel statoLabel;
    private boolean controIA; // true = contro IA, false = due giocatori

    private DiffAI ia; // l'IA (facile / medio / difficile)

    public OthelloGame(boolean controIA) {
        this(controIA, "medium");
    }

    public OthelloGame(boolean controIA, String difficolta) {
        this.controIA = controIA;
        this.ia = new DiffAI(difficolta);
        setTitle("Othello - Partita");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inizializzaTavola();

        JPanel principale = new JPanel(new BorderLayout());
        principale.setBackground(new Color(0, 100, 0));

        // Etichetta in alto con turno e punteggio
        statoLabel = new JLabel("", SwingConstants.CENTER);
        statoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        statoLabel.setForeground(Color.WHITE);
        statoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        principale.add(statoLabel, BorderLayout.NORTH);

        // Griglia di gioco 8x8
        JPanel griglia = new JPanel(new GridLayout(DIMENSIONE, DIMENSIONE, 2, 2));
        griglia.setBackground(Color.BLACK); // fa da linee della scacchiera
        griglia.setBorder(BorderFactory.createEmptyBorder(30, 250, 30, 250));

        for (int r = 0; r < DIMENSIONE; r++) {
            for (int c = 0; c < DIMENSIONE; c++) {
                JButton cella = new JButton();
                cella.setOpaque(true);
                cella.setBorderPainted(false);
                cella.setBackground(new Color(0, 100, 0));
                cella.setFont(new Font("Arial", Font.PLAIN, 36));
                cella.setFocusPainted(false);

                final int riga = r, colonna = c;
                cella.addActionListener(e -> gestisciMossaUmano(riga, colonna));

                celle[r][c] = cella;
                griglia.add(cella);
            }
        }
        principale.add(griglia, BorderLayout.CENTER);

        // Pulsante per tornare al menu
        JButton menuButton = new JButton("Torna al menu");
        menuButton.setFont(new Font("Arial", Font.BOLD, 20));
        menuButton.setBackground(Color.BLACK);
        menuButton.setForeground(Color.WHITE);
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> {
            dispose();
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });

        JPanel sud = new JPanel();
        sud.setBackground(new Color(0, 100, 0));
        sud.add(menuButton);
        principale.add(sud, BorderLayout.SOUTH);

        add(principale);
        aggiornaSchermo();
    }

    // Posiziona i 4 pezzi iniziali al centro, come nelle regole ufficiali
    private void inizializzaTavola() {
        for (int r = 0; r < DIMENSIONE; r++) {
            for (int c = 0; c < DIMENSIONE; c++) {
                tavola[r][c] = VUOTO;
            }
        }
        tavola[3][3] = BIANCO;
        tavola[3][4] = NERO;
        tavola[4][3] = NERO;
        tavola[4][4] = BIANCO;
    }

    // Chiamato quando il giocatore umano clicca su una cella
    private void gestisciMossaUmano(int riga, int colonna) {
        if (controIA && turno != NERO) return; // contro IA: il giocatore e' solo il Nero
        if (!mossaValida(riga, colonna, turno)) return;

        eseguiMossa(riga, colonna, turno);
        turno = (turno == NERO) ? BIANCO : NERO;
        aggiornaSchermo();
        procediTurno();
    }

    // Controlla chi deve giocare, gestisce i turni saltati e la fine partita
    private void procediTurno() {
        if (verificaFinePartita()) {
            mostraRisultato();
            return;
        }

        if (!esisteMossaValida(turno)) {
            // il giocatore di turno non puo' muovere: si passa
            turno = (turno == NERO) ? BIANCO : NERO;
            if (!esisteMossaValida(turno)) {
                mostraRisultato();
                return;
            }
        }

        aggiornaSchermo();

        if (controIA && turno == BIANCO) {
            // piccola pausa per dare l'impressione che l'IA stia "pensando"
            javax.swing.Timer timer = new javax.swing.Timer(500, e -> mossaIA());
            timer.setRepeats(false);
            timer.start();
        }
    }

    // L'IA sceglie la mossa usando la classe DiffAI (in base alla difficolta')
    private void mossaIA() {
        // Elenco delle mosse legali del Bianco
        List<Move> mosseValide = new ArrayList<>();
        for (int r = 0; r < DIMENSIONE; r++) {
            for (int c = 0; c < DIMENSIONE; c++) {
                if (mossaValida(r, c, BIANCO)) {
                    mosseValide.add(new Move(r, c));
                }
            }
        }

        // Regole di gioco che DiffAI usa per simulare le mosse
        GameLogic logica = (board, riga, colonna, colore) ->
                pezziDaGirare(riga, colonna, colore == 'O' ? BIANCO : NERO);

        Move mossa = ia.getBestMove(tavolaComeChar(), 'O', mosseValide, logica);
        if (mossa != null) {
            eseguiMossa(mossa.getRow(), mossa.getCol(), BIANCO);
        }

        turno = NERO;
        aggiornaSchermo();
        procediTurno();
    }

    // Converte la scacchiera in char[][] per DiffAI: 'X' = Nero, 'O' = Bianco, '.' = vuoto
    private char[][] tavolaComeChar() {
        char[][] copia = new char[DIMENSIONE][DIMENSIONE];
        for (int r = 0; r < DIMENSIONE; r++) {
            for (int c = 0; c < DIMENSIONE; c++) {
                if (tavola[r][c] == NERO) copia[r][c] = 'X';
                else if (tavola[r][c] == BIANCO) copia[r][c] = 'O';
                else copia[r][c] = '.';
            }
        }
        return copia;
    }

    // Verifica se piazzare un pezzo in (riga, colonna) e' una mossa legale
    private boolean mossaValida(int riga, int colonna, int giocatore) {
        if (riga < 0 || riga >= DIMENSIONE || colonna < 0 || colonna >= DIMENSIONE) return false;
        if (tavola[riga][colonna] != VUOTO) return false;

        int avversario = (giocatore == NERO) ? BIANCO : NERO;

        for (int d = 0; d < 8; d++) {
            int r = riga + DX[d];
            int c = colonna + DY[d];
            boolean trovatoAvversario = false;

            while (r >= 0 && r < DIMENSIONE && c >= 0 && c < DIMENSIONE && tavola[r][c] == avversario) {
                trovatoAvversario = true;
                r += DX[d];
                c += DY[d];
            }

            if (trovatoAvversario && r >= 0 && r < DIMENSIONE && c >= 0 && c < DIMENSIONE
                    && tavola[r][c] == giocatore) {
                return true;
            }
        }
        return false;
    }

    // Restituisce la lista dei pezzi avversari che verrebbero catturati con questa mossa
    private List<int[]> pezziDaGirare(int riga, int colonna, int giocatore) {
        int avversario = (giocatore == NERO) ? BIANCO : NERO;
        List<int[]> totale = new ArrayList<>();

        for (int d = 0; d < 8; d++) {
            int r = riga + DX[d];
            int c = colonna + DY[d];
            List<int[]> linea = new ArrayList<>();

            while (r >= 0 && r < DIMENSIONE && c >= 0 && c < DIMENSIONE && tavola[r][c] == avversario) {
                linea.add(new int[]{r, c});
                r += DX[d];
                c += DY[d];
            }

            if (!linea.isEmpty() && r >= 0 && r < DIMENSIONE && c >= 0 && c < DIMENSIONE
                    && tavola[r][c] == giocatore) {
                totale.addAll(linea);
            }
        }
        return totale;
    }

    // Piazza il pezzo e gira tutti i pezzi avversari catturati
    private void eseguiMossa(int riga, int colonna, int giocatore) {
        tavola[riga][colonna] = giocatore;
        int avversario = (giocatore == NERO) ? BIANCO : NERO;

        for (int d = 0; d < 8; d++) {
            int r = riga + DX[d];
            int c = colonna + DY[d];
            List<int[]> daGirare = new ArrayList<>();

            while (r >= 0 && r < DIMENSIONE && c >= 0 && c < DIMENSIONE && tavola[r][c] == avversario) {
                daGirare.add(new int[]{r, c});
                r += DX[d];
                c += DY[d];
            }

            if (!daGirare.isEmpty() && r >= 0 && r < DIMENSIONE && c >= 0 && c < DIMENSIONE
                    && tavola[r][c] == giocatore) {
                for (int[] cella : daGirare) {
                    tavola[cella[0]][cella[1]] = giocatore;
                }
            }
        }
    }

    private boolean esisteMossaValida(int giocatore) {
        for (int r = 0; r < DIMENSIONE; r++) {
            for (int c = 0; c < DIMENSIONE; c++) {
                if (mossaValida(r, c, giocatore)) return true;
            }
        }
        return false;
    }

    private boolean verificaFinePartita() {
        return !esisteMossaValida(NERO) && !esisteMossaValida(BIANCO);
    }

    // Ridisegna tutte le celle e aggiorna il testo del turno/punteggio
    private void aggiornaSchermo() {
        for (int r = 0; r < DIMENSIONE; r++) {
            for (int c = 0; c < DIMENSIONE; c++) {
                JButton cella = celle[r][c];
                if (tavola[r][c] == NERO) {
                    cella.setText("\u25CF");
                    cella.setForeground(Color.BLACK);
                } else if (tavola[r][c] == BIANCO) {
                    cella.setText("\u25CF");
                    cella.setForeground(Color.WHITE);
                } else {
                    cella.setText("");
                }
            }
        }

        int pezziNeri = contaPezzi(NERO);
        int pezziBianchi = contaPezzi(BIANCO);
        String turnoTesto;
        if (controIA) {
            turnoTesto = (turno == NERO) ? "Il tuo turno (Nero)" : "Turno dell'IA (Bianco)";
        } else {
            turnoTesto = (turno == NERO) ? "Turno del Nero" : "Turno del Bianco";
        }
        statoLabel.setText(turnoTesto + "   |   Nero: " + pezziNeri + "   Bianco: " + pezziBianchi);
    }

    private int contaPezzi(int giocatore) {
        int conteggio = 0;
        for (int r = 0; r < DIMENSIONE; r++) {
            for (int c = 0; c < DIMENSIONE; c++) {
                if (tavola[r][c] == giocatore) conteggio++;
            }
        }
        return conteggio;
    }

    private void mostraRisultato() {
        int pezziNeri = contaPezzi(NERO);
        int pezziBianchi = contaPezzi(BIANCO);
        String messaggio;

        if (pezziNeri > pezziBianchi) {
            messaggio = (controIA ? "Hai vinto!" : "Ha vinto il Nero!")
                    + " Nero: " + pezziNeri + " - Bianco: " + pezziBianchi;
        } else if (pezziBianchi > pezziNeri) {
            messaggio = (controIA ? "Ha vinto l'IA." : "Ha vinto il Bianco!")
                    + " Nero: " + pezziNeri + " - Bianco: " + pezziBianchi;
        } else {
            messaggio = "Pareggio! " + pezziNeri + " - " + pezziBianchi;
        }

        statoLabel.setText(messaggio);
        JOptionPane.showMessageDialog(this, messaggio, "Partita terminata", JOptionPane.INFORMATION_MESSAGE);
    }
}