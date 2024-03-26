import javax.swing.*;
import java.awt.*;

public class WindowGame {
    public static final int WIDTH= 500, HEIGHT = 632; //constanta
    private Board board;
    private JFrame window;

    private final Shapes shapes;
    private final Score score;
    private final Level level;

    public WindowGame(){
        window = new JFrame("Tetris");
        board = new Board();
        window.setSize(WIDTH,HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLocationRelativeTo(null);

        score = new Score();
        level = new Level(score);

        //asta este ca sa mearga shapes = new Shapes, pentru Constructor
        int[][] coords = {{1, 1, 1}, {0, 1, 0}};
        Color color = Color.RED;
        shapes = new Shapes(coords, board, color, score,level);

        window.add(board);
        window.addKeyListener(board);
        window.setVisible(true);
    }
    public static void main(String[] args){
        new WindowGame();
    }
}
