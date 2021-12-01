package frame.player;

import java.io.Serializable;

public class PlayerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private int winCount = 0, loseCount = 0;

    public PlayerInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getWinCount() {
        return winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public void addWinCount() {
        winCount++;
    }

    public void addLoseCount() {
        loseCount++;
    }
}
