import java.util.List;
import java.util.Random;

// Classe di supporto per rappresentare la mossa (riga, colonna)
class Move {
    private int row;
    private int col;

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}

// Interfaccia di esempio per la logica di gioco (da adattare al tuo codice esistente)
interface GameLogic {
    List<?> simulate_move(char[][] board, int row, int col, char playerColor);
}

public class DiffAI {
    private String difficulty;
    private Random random;

    // Costruttore con difficoltà predefinita a "medium"
    public DiffAI() {
        this.difficulty = "medium";
        this.random = new Random();
    }

    // Costruttore con livello di difficoltà personalizzato
    public DiffAI(String difficulty) {
        this.difficulty = difficulty != null ? difficulty : "medium";
        this.random = new Random();
    }

    /**
     * Restituisce la mossa migliore (oggetto Move) in base alla difficoltà scelta.
     * - board: stato attuale della scacchiera (es. char[][])
     * - playerColor: colore dell'IA ('O' o 'X')
     * - validMoves: lista di oggetti Move delle mosse legali disponibili
     * - game_logic: riferimento all'oggetto che gestisce le regole
     */
    public Move getBestMove(char[][] board, char playerColor, List<Move> validMoves, GameLogic gameLogic) {
        if (validMoves == null || validMoves.isEmpty()) {
            return null;
        }

        switch (difficulty.toLowerCase()) {
            case "easy":
                // Sceglie una mossa a caso tra quelle disponibili
                return validMoves.get(random.nextInt(validMoves.size()));

            case "medium": {
                // Trova la mossa che cattura più pedine
                Move bestMove = validMoves.get(0);
                int maxFlips = -1;

                for (Move move : validMoves) {
                    List<?> flippedDiscs = gameLogic.simulate_move(board, move.getRow(), move.getCol(), playerColor);
                    int count = flippedDiscs != null ? flippedDiscs.size() : 0;

                    if (count > maxFlips) {
                        maxFlips = count;
                        bestMove = move;
                    }
                }
                return bestMove;
            }

            case "hard": {
                // Euristica basata su una tabella di pesi posizionali
                int[][] weights = {
                    {100, -20,  10,   5,   5,  10, -20, 100},
                    {-20, -50,  -2,  -2,  -2,  -2, -50, -20},
                    { 10,  -2,   1,   1,   1,   1,  -2,  10},
                    {  5,  -2,   1,   1,   1,   1,  -2,   5},
                    {  5,  -2,   1,   1,   1,   1,  -2,   5},
                    { 10,  -2,   1,   1,   1,   1,  -2,  10},
                    {-20, -50,  -2,  -2,  -2,  -2, -50, -20},
                    {100, -20,  10,   5,   5,  10, -20, 100}
                };

                Move bestMove = validMoves.get(0);
                double maxScore = -Double.MAX_VALUE;

                for (Move move : validMoves) {
                    int r = move.getRow();
                    int c = move.getCol();
                    double score = weights[r][c];

                    // Bonus combinato con il numero di pedine catturate
                    List<?> flippedDiscs = gameLogic.simulate_move(board, r, c, playerColor);
                    int count = flippedDiscs != null ? flippedDiscs.size() : 0;
                    score += count * 2.0;

                    if (score > maxScore) {
                        maxScore = score;
                        bestMove = move;
                    }
                }
                return bestMove;
            }

            default:
                return validMoves.get(0);
        }
    }
}