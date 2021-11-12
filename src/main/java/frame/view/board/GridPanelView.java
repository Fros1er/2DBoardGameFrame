package frame.view.board;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class GridPanelView extends JPanel implements GridView {

    private final Map<String, Component> componentMap = new HashMap<>();

    @Override
    public abstract void init();

    @Override
    public abstract void redraw();

    public void addComponent(String name, Component comp) {
        componentMap.put(name, comp);
        this.add(comp);
    }
    public Component getComponentByName(String name) {
        return componentMap.get(name);
    }
}
