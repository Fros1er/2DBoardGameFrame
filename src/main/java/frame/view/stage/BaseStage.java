package frame.view.stage;

import frame.util.Procedure;

import javax.swing.*;

public abstract class BaseStage extends JPanel {
    private final String name;
    protected Procedure drawComponents;
    public BaseStage(String name) {
        this.name = name;
    }
    public void init() {
        drawComponents.invoke();
    }
    public void enter() {}
    public void exit() {}
    public String getName() {
        return name;
    }
    public void setCustomDrawMethod(Procedure drawMethod) {
        drawComponents = drawMethod;
    }
}
