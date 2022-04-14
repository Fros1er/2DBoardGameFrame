package frame.view.stage;

import frame.Controller.Game;
import frame.view.View;
import frame.view.components.BackgroundImagePanel;
import frame.view.components.RoomBlock;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class RoomStage extends BaseStage {
    private static volatile RoomStage sInstance = null;

    public final BackgroundImagePanel settingsRow = new BackgroundImagePanel();
    public final BackgroundImagePanel playerRows = new BackgroundImagePanel();
    public final BackgroundImagePanel buttonRows = new BackgroundImagePanel();

    public RoomBlock[] roomBlocks = null;
    public final JButton back = new JButton("Back");
    public final JButton start = new JButton("Start");
    public final JTextField textWidth = new JTextField(4);
    public final JTextField textHeight = new JTextField(4);

    private void parseSizeString() {
        if (textWidth.getText().isEmpty() || textHeight.getText().isEmpty()) return;
        Game.setBoardSize(Integer.parseInt(textWidth.getText()), Integer.parseInt(textHeight.getText()));
    }

    private RoomStage() {
        super("RoomStage");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        back.addActionListener((e) -> View.changeStage("MenuStage"));
        start.addActionListener((e) -> View.changeStage("GameStage"));

        DocumentListener sizeListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                parseSizeString();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                parseSizeString();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                parseSizeString();
            }
        };
        textWidth.getDocument().addDocumentListener(sizeListener);
        textHeight.getDocument().addDocumentListener(sizeListener);

        playerRows.setLayout(new BoxLayout(playerRows, BoxLayout.Y_AXIS));

        drawComponents = () -> {
            this.add(settingsRow);
            this.add(playerRows);
            this.add(buttonRows);
            settingsRow.add(textWidth);
            settingsRow.add(textHeight);
            buttonRows.add(back);
            buttonRows.add(start);
        };
    }

    @Override
    public void init() {
        super.init();
        textWidth.setText(String.valueOf(Game.getWidth()));
        textHeight.setText(String.valueOf(Game.getHeight()));
        roomBlocks = new RoomBlock[Game.getMaximumPlayerNumber()];
    }

    @Override
    public void enter() {
        for (int i = 0; i < Game.getMaximumPlayerNumber(); i++) {
            roomBlocks[i] = new RoomBlock(i);
            playerRows.add(roomBlocks[i]);
        }
    }

    @Override
    public void exit() {
        playerRows.removeAll();
    }

    public static RoomStage instance() {
        if (sInstance == null) {
            synchronized (BaseStage.class) {
                if (sInstance == null) {
                    sInstance = new RoomStage();
                }
            }
        }
        return sInstance;
    }
}
