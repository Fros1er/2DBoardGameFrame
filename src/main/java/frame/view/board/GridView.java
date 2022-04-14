package frame.view.board;

import frame.board.BaseGrid;

public interface GridView {
    void init();
    void redraw(BaseGrid grid);
}
