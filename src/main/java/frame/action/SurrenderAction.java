package frame.action;

import frame.event.EventCenter;
import frame.event.PlayerLoseEvent;
import frame.player.Player;

public class SurrenderAction extends Action{
    private final Player owner;
    public SurrenderAction(Player owner) {
        super(true);
        this.owner = owner;
    }

    @Override
    public ActionPerformType perform() {
        if (!owner.isOut()) {
            setChangedPlayer(owner);
            owner.lose();
            EventCenter.publish(new PlayerLoseEvent(this, owner));
        }
        return ActionPerformType.SUCCESS;
    }

    @Override
    public void undo() {
        owner.revive();
    }
}
