package frame.save;

import frame.Game;
import frame.action.Action;

import java.io.Serializable;
import java.util.Stack;

public class Save implements Serializable {

    public static final long serialVersionUID = 1L;

    public Stack<Action> actionStack;

    public int width, height;

    public Save(Stack<Action> actionStack) {
        this.actionStack = actionStack;
        width = Game.getWidth();
        height = Game.getHeight();
    }
}
