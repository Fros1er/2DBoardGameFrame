package frame.view.board;

import javax.swing.*;
import java.awt.*;

public abstract class BoardView extends JPanel {
    public BoardView() {
        super(new GridBagLayout());
    }
    public abstract void redraw();
}
