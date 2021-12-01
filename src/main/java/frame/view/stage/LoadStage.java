package frame.view.stage;


import frame.Game;
import frame.socket.OnlineType;
import frame.view.View;

import javax.swing.*;

public class LoadStage extends BaseStage {
    private static volatile LoadStage sInstance = null;

    public JLabel title = new JLabel("LOAD");
    public final JButton back = new JButton("Back");

    private int slotNumber = 3;
    public JButton[] saveButtons;

    private LoadStage() {
        super("LoadStage");
        this.add(title);
        back.addActionListener((e) -> View.changeStage("MenuStage"));
        this.add(back);
    }

    @Override
    public void init() {
        title.setText(View.title);
        saveButtons = new JButton[slotNumber];
        for (int i = 0; i < slotNumber; i++) {
            JButton load = new JButton("Load " + (i+1));
            int finalI = i;
            load.addActionListener((e) -> {
                Game.loadGame(String.format("saves/save%d.sav", finalI + 1));
                View.changeStage("GameStage");
            });
            this.add(load);
            saveButtons[i] = load;
        }
    }

    @Override
    public void exit() {
        Game.setOnlineType(OnlineType.NONE);
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
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
