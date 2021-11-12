package frame.view.board;

import frame.board.Grid;

@FunctionalInterface
public interface GridViewFactory <T extends Grid> {
    GridView createGridView(T grid);
}
