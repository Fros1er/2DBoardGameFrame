package frame.view.stage;

import javax.swing.*;

public abstract class BaseStage extends JPanel {
    private final String name;
    public BaseStage(String name) {
        this.name = name;
    }
    public void init() {}
    public void enter() {}
    public void exit() {}
    public String getName() {
        return name;
    }
}
