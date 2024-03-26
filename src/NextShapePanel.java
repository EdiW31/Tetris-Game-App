import javax.swing.*;
import java.awt.*;

public class NextShapePanel extends JPanel {
    private Shapes nextShape;

    public NextShapePanel() {
        setLayout(null);
        setBounds(345, 80, 120, 120);
        setPreferredSize(new Dimension(120, 120)); // seteaza marimea la board ul de next shape
        setBackground(Color.black);
    }

    public void setNextShape(Shapes nextShape) {
        this.nextShape = nextShape;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (nextShape != null) {
            // deseneaza nextshape in panel ul acela
            nextShape.drawNextShape(g);
        }
    }
}