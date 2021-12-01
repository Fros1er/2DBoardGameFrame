package frame.action;

import frame.Game;
import frame.player.Player;

public class SurrenderAction extends Action{
    private final Player owner;
    public SurrenderAction(Player owner) {
        super(true);
        this.owner = owner;
    }

    @Override
    public boolean perform() {
        if (!owner.isOut()) {
            setChangedPlayer(owner);
            owner.lose();
        }
        return true;
    }

    @Override
    public void undo() {

    }
}
