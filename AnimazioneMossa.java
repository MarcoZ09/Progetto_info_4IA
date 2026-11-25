import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

// Un solo orologio Swing per tutte le pedine coinvolte nella stessa mossa.
class AnimazioneMossa {
    private final Casella[][] celle;
    private Timer timer;

    AnimazioneMossa(Casella[][] celle) {
        this.celle = celle;
    }

    boolean inCorso() {
        return timer != null;
    }

    void avvia(int[][] tavola, int riga, int colonna, Runnable alTermine) {
        List<Casella> mosse = new ArrayList<>();
        for (int r = 0; r < celle.length; r++) {
            for (int c = 0; c < celle[r].length; c++) {
                int distanza = Math.max(Math.abs(r - riga), Math.abs(c - colonna));
                if (celle[r][c].prepara(tavola[r][c], distanza)) mosse.add(celle[r][c]);
            }
        }
        final long inizio = System.nanoTime();
        timer = new Timer(16, e -> {
            long trascorsi = (System.nanoTime() - inizio) / 1_000_000;
            mosse.removeIf(casella -> !casella.avanza(trascorsi));
            if (mosse.isEmpty()) {
                ferma();
                alTermine.run();
            }
        });
        timer.start();
    }

    void ferma() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }
}
