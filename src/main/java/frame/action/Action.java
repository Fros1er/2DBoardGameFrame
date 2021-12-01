package frame.action;

import frame.player.Player;

import java.io.Serializable;

public abstract class Action implements Serializable {

    public static final long serialVersionUID = 1L;

    public final boolean endTurn;
    private Player changedPlayer;

    public Action(boolean endTurn) {
        this.endTurn = endTurn;
    }

    public abstract boolean perform();

    public abstract void undo();

    public Player getChangedPlayer() {
        return changedPlayer;
    }

    public void setChangedPlayer(Player changedPlayer) {
        this.changedPlayer = changedPlayer;
    }
}
