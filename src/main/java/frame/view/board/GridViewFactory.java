package frame.view.board;

import frame.board.BaseGrid;

@FunctionalInterface
public interface GridViewFactory {
    GridView createGridView();
}
