import java.awt.*;

public class Shapes {
    private int Score;
    private int x = 4, y = 0;
    private final int normal = 600;
    private int fast = 50;
    private int delayTimeMovement = normal;
    private long beginTime;
    //miscare stanga dreapta a piesei tetris
    private int deltaX = 0;
    private boolean colision = false;

    private int[][] coords;
    private Board board;
    private Color color;
    private Score score;
    private Level level;

    //aici este incapsulare
    public Shapes(int[][] coords, Board board, Color color, Score score, Level level) {
        this.coords = coords;
        this.board = board;
        this.color = color;
        this.score = score;
        this.level = level;
    }

    // Constructor nou pentru a copia forma si a o stoca in NextPanel ca atunci cand am aceasi piesa sa nu se intoarca
    // si in nextpanel.
    public Shapes(Shapes other) {
        this.x = other.x;
        this.y = other.y;
        this.Score = other.Score;
        this.delayTimeMovement = other.delayTimeMovement;
        this.beginTime = other.beginTime;
        this.deltaX = other.deltaX;
        this.colision = other.colision;
        this.coords = new int[other.coords.length][other.coords[0].length];
        for (int row = 0; row < other.coords.length; row++) {
            System.arraycopy(other.coords[row], 0, this.coords[row], 0, other.coords[row].length);
        }
        this.board = other.board;
        this.color = other.color;
    }

    public void reset() {
        this.x = 4;
        this.y = 0;
        colision = false;
    }

    public void drawNextShape(Graphics g) {
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    int x = col * Board.BLOCK_SIZE + 10; // Adjust the position.
                    int y = row * Board.BLOCK_SIZE + 10; // Adjust the position.
                    g.setColor(color);
                    g.fillRect(x, y, Board.BLOCK_SIZE, Board.BLOCK_SIZE);
                }
            }
        }
    }
    public void update() {
        if (colision) {
            //culoarea pentru board
            for (int row = 0; row < coords.length; row++) {
                for (int col = 0; col < coords[0].length; col++) {
                    if (coords[row][col] != 0) {
                        board.getBoard()[y + row][x + col] = color;
                    }
                }
            }
            //folosim metoda verificaLinia pentru a sterge ce este plin
            verificaLinia();

            //aici am folosit metoda din Board.java
            board.setCurentShaper();
            return;
        }
        //verifica daca trece de board orizontal
        boolean moveX = true;
        //verifica daca piesa nu depaseste latimea board ului
        if (!(x + deltaX + coords[0].length > 10) && !(x + deltaX < 0)) {
            //verifica daca piesa se intersecteaza cu alta
            for (int row = 0; row < coords.length; row++) {
                for (int col = 0; col < coords[row].length; col++) {
                    if (coords[row][col] != 0) {
                        if (board.getBoard()[y + row][x + deltaX + col] != null) {
                            moveX = false;
                        }
                    }
                }
            }
            if (moveX) {
                x += deltaX;
            }
        }
        deltaX = 0;

        if (System.currentTimeMillis() - beginTime > delayTimeMovement) {
            //miscare verticala care este automata
            if (!(y + 1 + coords.length > Board.BOARD_HEIGHT)) {
                for (int row = 0; row < coords.length; row++) {
                    for (int col = 0; col < coords[row].length; col++) {
                        if (coords[row][col] != 0) {
                            if (board.getBoard()[y + 1 + row][x + deltaX + col] != null) {
                                colision = true;
                            }
                        }
                    }
                }
                if (!colision) {
                    y++;
                }
            } else {
                colision = true;
            }
            beginTime = System.currentTimeMillis();
        }
    }

    public void afisareForma(Graphics g) {
        //deseneaza forma pe care am scris o mai sus in color shape
        for (int row = 0; row < coords.length; row++) {
            for (int column = 0; column < coords[0].length; column++) {
                if (coords[row][column] != 0) {
                    g.setColor(color);
                    g.fillRect(column * Board.BLOCK_SIZE + x * Board.BLOCK_SIZE, row * Board.BLOCK_SIZE + y * Board.BLOCK_SIZE, Board.BLOCK_SIZE, Board.BLOCK_SIZE);
                }
            }
        }
    }

    //rotatia unei piese de tetris;
    public void verificaPosibilitateRotatie() {
        int newDeltaX = deltaX; // stocheaza delta x

        if (x + deltaX + coords[0].length > 10) {
            // Calculeaza maximu necesar pentrnu a ramane piesa intre bordura dreapta
            newDeltaX = 10 - x - coords[0].length;
        }

        if (x + deltaX < 0) {
            // Calculeaza maximu necesar pentrnu a ramane piesa intre bordura stanga
            newDeltaX = -x;
        }

        //Creaza o copie pentru piesa curente dupa rotatie
        int[][] rotatedCoords = transpuneMatrix(coords);
        reverseRow(rotatedCoords);

        // verifica daca detaX are o colision cu alte forme din tetris
        for (int row = 0; row < rotatedCoords.length; row++) {
            for (int col = 0; col < rotatedCoords[row].length; col++) {
                if (rotatedCoords[row][col] != 0) {
                    if (board.getBoard()[y + row][x + newDeltaX + col] != null) {
                        return;
                    }
                }
            }
        }

        // daca nu este nici o colision atunci aplica rotatia si updates deltaX
        deltaX = newDeltaX;
        coords = rotatedCoords;
    }

    //asta invarte matricea de exemplu la l shape o face sa fie din 3/2 in 2/3 ca sa poata sa fie facut L in picioare
    private int[][] transpuneMatrix(int[][] matrix) {
        //temp este o matrice temporara cu nr randurilor al matricei originale
        int[][] temp = new int[matrix[0].length][matrix.length];
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                //atribuie valorile corespunzatoare din matricea initiala in matricea temporara
                temp[col][row] = matrix[row][col];
            }
        }
        //returneaza matricea temporara
        return temp;
    }

    private void reverseRow(int[][] matrix) {
        int middle = matrix.length / 2;
        for (int row = 0; row < middle; row++) {
            int[] temp = matrix[row];
            matrix[row] = matrix[matrix.length - row - 1];
            matrix[matrix.length - row - 1] = temp;
        }
    }

    //verifica linia daca este plina si o sterge
    private void verificaLinia() {
        int linieFinal = board.getBoard().length - 1;
        for (int linieInceput = board.getBoard().length - 1; linieInceput > 0; linieInceput--) {
            int count = 0;
            for (int col = 0; col < board.getBoard()[0].length; col++) {
                if (board.getBoard()[linieInceput][col] != null) {
                    count++;
                }
                board.getBoard()[linieFinal][col] = board.getBoard()[linieInceput][col];
            }
            if (count < board.getBoard()[0].length) {
                linieFinal--;
            } else {
                score.increaseScore(100);
            }
        }
    }

    public void VitezaForma() {
        delayTimeMovement = fast;
    }
    public void IncetinireForme() {
        delayTimeMovement = normal;
    }
    public void MutaDreapta() {
        deltaX = 1;
    }
    public void MutaStanga() {
        deltaX = -1;
    }
    public void resetScore() {
        score.resetScore();
    }
    public int[][] getCoords() {
        return coords;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getLevel() {
        return level.calculateLevel();
    }
}