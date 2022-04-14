package frame.action;

import frame.player.Player;

import java.io.Serializable;

public abstract class Action implements Serializable {

    public static final long serialVersionUID = 1L;

    public final boolean endTurn;
    private Player changedPlayer;
    public ActionPerformType type;

    public Action(boolean endTurn) {
        this.endTurn = endTurn;
    }

    public abstract ActionPerformType perform();

    public abstract void undo();

    public void removePending() {

    }

    public Player getChangedPlayer() {
        return changedPlayer;
    }

    public void setChangedPlayer(Player changedPlayer) {
        this.changedPlayer = changedPlayer;
    }
}
