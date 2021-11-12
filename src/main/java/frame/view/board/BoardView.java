package frame.view.board;

import javax.swing.*;
import java.awt.*;

public abstract class BoardView extends JPanel {
    public BoardView(int rows, int cols) {
        super(new GridLayout(rows, cols));
    }
    public abstract void redraw();
}
