package frame.event;

import java.util.EventObject;

public class PlayerChangeEvent extends EventObject {
    public final int id;
    public PlayerChangeEvent(Object source, int id) {
        super(source);
        this.id = id;
    }
}
