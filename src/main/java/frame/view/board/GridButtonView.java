package frame.view.board;

import javax.swing.*;

public abstract class GridButtonView extends JButton implements GridView {

    @Override
    public abstract void init();

    @Override
    public abstract void redraw();


}
