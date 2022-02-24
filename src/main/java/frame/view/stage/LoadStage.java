package frame.view.stage;


import frame.Game;
import frame.view.View;

import javax.swing.*;
import java.awt.*;

public class LoadStage extends BaseStage {
    private static volatile LoadStage sInstance = null;

    public JLabel title = new JLabel("LOAD");
    public final JButton back = new JButton("Back");
    public Box buttonPanel = new Box(BoxLayout.Y_AXIS);
    public JPanel dummyPanel = new JPanel();
    public JButton[] saveButtons;

    private LoadStage() {
        super("LoadStage");
        setLayout(new BorderLayout());
        title.setFont(new Font("Arial", Font.PLAIN, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        back.addActionListener((e) -> View.changeStage("MenuStage"));
        drawComponents = () -> {
            this.add(dummyPanel);
            this.add("North", title);
            saveButtons = new JButton[Game.getSlotNumber()];
            for (int i = 0; i < Game.getSlotNumber(); i++) {
                JButton load = new JButton("Load " + (i+1));
                int finalI = i;
                load.addActionListener((e) -> {
                    Game.loadGame(String.format("saves/save%d.sav", finalI + 1));
                    View.changeStage("GameStage");
                });
                buttonPanel.add(Box.createVerticalStrut(10));
                buttonPanel.add(load);
                saveButtons[i] = load;
            }
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(back);
            dummyPanel.add(buttonPanel);
        };
    }

    public static LoadStage instance() {
        if (sInstance == null) {
            synchronized (BaseStage.class) {
                if (sInstance == null) {
                    sInstance = new LoadStage();
                }
            }
        }
        return sInstance;
    }
}
