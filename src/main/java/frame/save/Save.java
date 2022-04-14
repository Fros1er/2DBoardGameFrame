package frame.save;

import frame.Controller.Game;
import frame.action.Action;

import java.io.Serializable;
import java.util.Stack;

public class Save implements Serializable {

    public Class<?> boardClass;

    public Stack<Action> actionStack;

    public int width, height;

    public Save(Class<?> boardClass, Stack<Action> actionStack) {
        this.boardClass = boardClass;
        this.actionStack = actionStack;
        width = Game.getWidth();
        height = Game.getHeight();
    }
}
