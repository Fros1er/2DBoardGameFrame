package frame.action;

import frame.board.Grid;
import frame.player.Player;

public interface ActionFactory <T extends Grid> {
    //1: left
    //2: right
    //3: mid
    Action createAction(T grid, int x, int y, int button);
}
