package frame.view.stage;


import frame.view.View;

import javax.swing.*;

    /* -------- menu stage ----------------
    by default it should be like:

            G A M E N A M E
                    New Game          // jump to roomStage or gameStage, depending on setting
                    Load              // jump to load&save stage
                    Settings          // jump to setting stage
                    quit              // quit game

    */

public class MenuStage extends BaseStage {
    private static volatile MenuStage sInstance = null;

    public JLabel title = new JLabel("Menu");
    public JButton newGame = new JButton("New Game");
    public JButton load = new JButton("Load");
    public JButton settings = new JButton("Settings");
    public JButton quit = new JButton("Quit");

    private MenuStage() {
        super("MenuStage");
        this.add(title);
        this.add(newGame);
        this.add(load);
        this.add(settings);
        this.add(quit);

        newGame.addActionListener((e) -> View.changeStage("RoomStage"));
        load.addActionListener((e) -> View.changeStage("LoadStage"));
        settings.addActionListener((e) -> View.changeStage("SettingStage"));
        quit.addActionListener((e) -> View.window.dispose());
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
