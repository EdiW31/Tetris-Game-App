import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Board extends JPanel implements KeyListener {
    private static int FPS = 60;
    private static int delay = FPS / 1000;

    public static final int BOARD_WIDTH = 10; //constanta
    public static final int BOARD_HEIGHT = 20; //constanta
    public static final int BLOCK_SIZE = 30; //constanta

    public int START_GAME_PLAY = 0;
    public int START_GAME_PAUSE = 1;
    public int START_GAME_OVER = 2;

    private int stare = START_GAME_PLAY;
    private Random random;
    private Timer looper;
    //se declara o matrice  bidimensionala, adica tabla de joc
    private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
    private NextShapePanel nextShapePanel;
    private Shapes nextShape;
    private Shapes nextShapeCopy;
    private Shapes[] forme = new Shapes[7];
    private Shapes currentShape;
    private Score score;
    private Level level;

    private Color[] colors = {
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.BLUE,
            Color.decode("#a349a4"),
            /* Culoarea este Violet dar
            interfata Color nu are violet ca si metoda. */
            Color.decode("#3f48cc")
            /*Asta este u albastru
            violet care la fel.. nu exista ca metoda*/
    };

    //aici incepe Board ul, constructorul
    public Board() {
        nextShapePanel = new NextShapePanel();
        random = new Random();
        score = new Score();
        level = new Level(score);
        //Toate Piesele din Tetris
        forme[0] = new Shapes(new int[][] { //forma dreapta(straight)
                {1, 1, 1, 1}
        }, this, colors[0], score, level);

        forme[1] = new Shapes(new int[][] { //t-shaped
                {1, 1, 1},
                {0, 1, 0}
        }, this, colors[1], score, level);
        forme[2] = new Shapes(new int[][] { //L-shaped
                {1, 1, 1},
                {1, 0, 0}
        }, this, colors[2], score, level);
        forme[3] = new Shapes(new int[][] { //reverse L-shaped
                {1, 1, 1},
                {0, 0, 1}
        }, this, colors[3], score, level);
        forme[4] = new Shapes(new int[][] { //cub
                {1, 1},
                {1, 1}
        }, this, colors[4], score, level);
        forme[5] = new Shapes(new int[][] { //zig-zag
                {1, 1, 0},
                {0, 1, 1}
        }, this, colors[5], score, level);
        forme[6] = new Shapes(new int[][] { //reversed zig zag
                {0, 1, 1},
                {1, 1, 0}
        }, this, colors[6], score, level);
        //selectam forma cu care sa inceapa tetris
        currentShape = forme[0];

        //face ca tabla sa fie goala de fiecare data cand incepe jocul
        setLayout(null);
        //adaugam nextShapePanel
        add(nextShapePanel);

        //looper este foarte important pentru ca actualizeaza jocul la fiecare secunda, mereu face repaint
        //adica si update, jocul isi da "update" de fiecare data cand trece o secunda
        looper = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }
        });
        looper.start();
        generateNextShape();
    }

    //aici apelam update din Shapes
    private void update() {
        if (stare == START_GAME_PLAY) {
            currentShape.update();
        }
    }

    private void generateNextShape() {
        nextShape = forme[random.nextInt(forme.length)];
        nextShapeCopy = new Shapes(nextShape);
        nextShapePanel.setNextShape(nextShapeCopy);
    }

    //schimba forma si e public pentru a putea sa o folosesc in clasa Shapes;
    public void setCurentShaper() {
        currentShape = nextShape;
        currentShape.reset();
        checkIfGameOver();
        nextShapeCopy = new Shapes(nextShape);
        nextShapePanel.setNextShape(nextShapeCopy);
        generateNextShape();
    }

    public void checkIfGameOver() {
        int[][] coords = currentShape.getCoords();
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    if (board[row + currentShape.getY()][col + currentShape.getX()] != null) {
                        stare = START_GAME_OVER;
                    }
                }
            }
        }
        if (stare == START_GAME_OVER) {
            generateNextShape(); //seteaza nextshape cand reincepe programu
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        //deseneaza forma pe board
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        currentShape.afisareForma(g);

        //pentru a schimba culoarea cand sunt jos si sa nu treaca de fund
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] != null) {
                    g.setColor(board[row][col]);
                    g.fillRect(col * BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        //deseneaza board ul de joc
        g.setColor(Color.white);
        for (int row = 0; row <= BOARD_HEIGHT; row++) {
            g.drawLine(0, BLOCK_SIZE * row, BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
        }
        for (int column = 0; column <= BOARD_WIDTH; column++) {
            g.drawLine(column * BLOCK_SIZE, 0, column * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
        }
        if (nextShapePanel != null) {
            nextShapePanel.repaint();
        }

        //daca game over atunci sa afiseze pe ecran game over;
        if (stare == START_GAME_PLAY) {
            g.setColor(Color.green);
            g.drawString("WELCOME TO TETRIS", 340, 290);
            g.drawString("Press 'P' to pause!", 340, 310);
        }
        if (stare == START_GAME_OVER) {
            g.setColor(Color.red);
            g.drawString("GAME OVER!", 340, 290);
            g.drawString("Press SPACE to start again!", 320, 310);
        }
        if (stare == START_GAME_PAUSE) {
            g.setColor(Color.orange);
            g.drawString("GAME PAUSED!", 340, 290);
            g.drawString("Press 'P' to resume!", 340, 310);
        }


        g.setColor(Color.white);
        Font BoldFont = new Font("Arial", Font.BOLD, 40);
        Font SemiBoldFont = new Font("Arial", Font.BOLD, 30);

        g.setFont(SemiBoldFont);
        g.drawString("Level: ", 340, 410);
        int level = currentShape.getLevel();
        String levelText = Integer.toString(level);
        g.drawString(levelText, 440, 410);

        g.setFont(BoldFont);
        g.drawString("Score", 340, 460);
        int scoreValue = score.getScore();
        String scoreText = Integer.toString(scoreValue);

        g.drawString(scoreText, 384, 510);
    }

    public Color[][] getBoard() {
        return board;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        ;
    }

    //cand apesi space se misca mult mai rapid
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            currentShape.VitezaForma();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            currentShape.MutaDreapta();
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            currentShape.MutaStanga();
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            currentShape.verificaPosibilitateRotatie();
        }

        if (stare == START_GAME_OVER) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                for (int row = 0; row < board.length; row++) {
                    for (int col = 0; col < board[0].length; col++) {
                        board[row][col] = null;
                    }
                }
                setCurentShaper();
                currentShape.resetScore();
                stare = START_GAME_PLAY;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_P) {
            if (stare == START_GAME_PLAY) {
                stare = START_GAME_PAUSE;
            } else if (stare == START_GAME_PAUSE) {
                stare = START_GAME_PLAY;
            }
        }
    }

    //cand nu mai apesi se misca nomral
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_SPACE) {
            currentShape.IncetinireForme();
        }
    }
}