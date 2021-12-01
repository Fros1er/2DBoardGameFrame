package frame.player;

import frame.Game;
import frame.action.SurrenderAction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Player implements Serializable {

    public static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private boolean isReady;
    protected final PlayerType type;
    private boolean hasWin;
    private boolean hasLose;
    public boolean countInfo;

    public Player(int id, String name, PlayerType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void onNotify() {
    }

    public int getId() {
        return id;
    }

    public void setReady(boolean state) {
        isReady = state;
    }

    public boolean isReady() {
        return isReady;
    }

    public String getName() {
        return name;
    }

    public PlayerType getType() {
        return type;
    }

    public void lose() {
        hasLose = true;
    }

    public void win() {
        hasWin = true;
    }

    public boolean isWin() {
        return hasWin;
    }

    public boolean isLose() {
        return hasLose;
    }

    public boolean isOut() {
        return hasWin || hasLose;
    }

    public void revive() {
        hasWin = false;
        hasLose = false;
    }

    public void surrender() {
        Game.performAction(new SurrenderAction(this));
    }

    protected static Player playerFactory(int id, String name, PlayerType type) {
        Player res;
        switch (type) {
            case AI:
                res = AIPlayer.getAIPlayer(id, name);
                break;
            case REMOTE:
                res = new RemotePlayer(id, name);
                break;
            default:
                res = new LocalPlayer(id, name);
        }
        return res;
    }
}
