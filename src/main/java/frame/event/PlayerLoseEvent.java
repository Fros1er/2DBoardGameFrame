package frame.event;

import frame.player.Player;

import java.util.EventObject;

public class PlayerLoseEvent extends EventObject {
    private final Player player;
    public PlayerLoseEvent(Object source, Player player) {
        super(source);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
