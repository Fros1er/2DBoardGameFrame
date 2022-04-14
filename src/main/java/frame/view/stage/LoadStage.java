package frame.view.stage;


import frame.Controller.Game;
import frame.save.UnmatchedSizeException;
import frame.view.View;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Function;

public class LoadStage extends BaseStage {
    private static volatile LoadStage sInstance = null;

    public JLabel title = new JLabel("LOAD");
    public final JButton back = new JButton("Back");
    public Box buttonPanel = new Box(BoxLayout.Y_AXIS);
    public JPanel dummyPanel = new JPanel();
    public JButton[] saveButtons;
    public Function<Exception, String> loadFailedMessageBuilder = (e) -> "Load failed: " + e.getMessage();

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
                JButton load = new JButton("Load " + (i + 1));
                int finalI = i;
                load.addActionListener((e) -> {
                    try {
                        Game.saver.load(String.format("saves/save%d.sav", finalI + 1));
                    } catch (IOException | ClassNotFoundException | UnmatchedSizeException ex) {
                        JOptionPane.showMessageDialog(load, loadFailedMessageBuilder.apply(ex));
                        return;
                    }
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
