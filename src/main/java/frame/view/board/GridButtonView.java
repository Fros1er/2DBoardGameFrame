package frame.view.board;

import frame.board.BaseGrid;

import javax.swing.*;

public abstract class GridButtonView extends JButton implements GridView {

    @Override
    public abstract void init();

    @Override
    public abstract void redraw(BaseGrid grid);


}
