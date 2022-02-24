package frame.view.stage;


import frame.Game;
import frame.view.View;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class MenuStage extends BaseStage {
    private static volatile MenuStage sInstance = null;

    public JLabel title = new JLabel("Menu");
    public JButton newGame = new JButton("New Game");
    public JButton load = new JButton("Load");
    public JButton settings = new JButton("Settings");
    public JButton rank = new JButton("Ranking");
    public JButton quit = new JButton("Quit");

    public Box buttonPanel = new Box(BoxLayout.Y_AXIS);
    public JPanel dummyPanel = new JPanel();

    private MenuStage() {
        super("MenuStage");
        setLayout(new BorderLayout());
        title.setFont(new Font("Arial", Font.PLAIN, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        settings.setVisible(false);

        newGame.addActionListener((e) -> View.changeStage("RoomStage"));
        load.addActionListener((e) -> View.changeStage("LoadStage"));
        rank.addActionListener((e) -> View.changeStage("RankingStage"));
        settings.addActionListener((e) -> View.changeStage("SettingStage"));
        quit.addActionListener((e) -> View.window.dispose());

        drawComponents = () -> {
            this.add(dummyPanel);
            this.add("North", title);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(newGame);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(load);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(rank);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(settings);
            buttonPanel.add(Box.createVerticalStrut(10));
            buttonPanel.add(quit);
            buttonPanel.add(Box.createVerticalGlue());
            dummyPanel.add(buttonPanel);
        };
    }

    @Override
    public void init() {
        super.init();
        revalidate();
        repaint();
    }

    public static MenuStage instance() {
        if (sInstance == null) {
            synchronized (BaseStage.class) {
                if (sInstance == null) {
                    sInstance = new MenuStage();
                }
            }
        }
        return sInstance;
    }
}
