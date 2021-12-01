package frame.view.board;

import frame.board.BaseGrid;

@FunctionalInterface
public interface GridViewFactory <T extends BaseGrid> {
    GridView createGridView(T grid);
}
