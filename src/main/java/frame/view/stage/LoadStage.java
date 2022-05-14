package frame.view.stage;


import frame.Controller.Game;
import frame.save.UnmatchedSizeException;
import frame.view.View;
import frame.view.components.BackgroundImagePanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class LoadStage extends BaseStage {
    private static volatile LoadStage sInstance = null;

    public JLabel title = new JLabel("LOAD");
    public final JButton back = new JButton("Back");
    public Box buttonPanel = new Box(BoxLayout.Y_AXIS);

    public BackgroundImagePanel dummyPanel = new BackgroundImagePanel();
    public JButton[] saveButtons;
    public JButton fileChooserButton = new JButton("Select file");
    public JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

    public Function<Exception, String> loadFailedMessageBuilder = (e) -> "Load failed: " + e.getMessage();

    private LoadStage() {
        super("LoadStage");
        setLayout(new BorderLayout());
        title.setFont(new Font("Arial", Font.PLAIN, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        back.addActionListener((e) -> View.changeStage("MenuStage"));

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("save", ".sav"));
        fileChooserButton.addActionListener(e -> {
            int res = fileChooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Game.saver.load(file.getPath());
                } catch (IOException | ClassNotFoundException | UnmatchedSizeException ex) {
                    JOptionPane.showMessageDialog(fileChooserButton, loadFailedMessageBuilder.apply(ex));
                    return;
                }
                View.changeStage("GameStage");
            }
        });
        drawComponents = () -> {
            this.add(dummyPanel);
            this.add("North", title);
            saveButtons = new JButton[Game.saver.getSlotNumber()];
            for (int i = 0; i < Game.saver.getSlotNumber(); i++) {
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
            buttonPanel.add(fileChooserButton);
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
