package frame.action;

import frame.player.Player;

public abstract class Action {
    public final boolean endTurn;

    public Action(boolean endTurn) {
        this.endTurn = endTurn;
    }

    public abstract boolean perform();
}
