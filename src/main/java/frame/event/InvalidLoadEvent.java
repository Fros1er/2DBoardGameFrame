package frame.event;

import java.util.EventObject;

public class InvalidLoadEvent extends EventObject {
    public InvalidLoadEvent(Object source) {
        super(source);
    }
}
