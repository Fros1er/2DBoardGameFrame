package frame.event;

import frame.player.Player;

import java.util.EventObject;

public class PlayerWinEvent extends EventObject {
    private final Player player;
    public PlayerWinEvent(Object source, Player player) {
        super(source);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
