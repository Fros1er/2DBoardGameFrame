package frame.board;

import frame.action.ActionFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Grid {
    public final int x, y;
    public final List<ActionFactory> actionFactoryList = new ArrayList<>();
    public Grid(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
