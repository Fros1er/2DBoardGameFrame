package frame.view.board;

import frame.Game;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class GridPanelView extends JPanel implements GridView {

    public final JLabel label = new JLabel();

    public GridPanelView() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(label);
        add(Box.createHorizontalGlue());
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    @Override
    public abstract void init();

    @Override
    public abstract void redraw();

    @Override
    public Dimension getPreferredSize() {
        Dimension space = getParent().getSize();
        int length = (Math.min(space.width / Game.getWidth(), space.height / Game.getHeight()));
        return new Dimension(length, length);
    }
}
