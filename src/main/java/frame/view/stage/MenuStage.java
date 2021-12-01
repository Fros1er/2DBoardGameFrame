package frame.view.stage;


import frame.Game;
import frame.socket.Client;
import frame.socket.OnlineType;
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
    public JButton findServer = new JButton("Find Server");
    public JButton settings = new JButton("Settings");
    public JButton rank = new JButton("Ranking");
    public JButton quit = new JButton("Quit");

    public JTextField remoteName = new JTextField();
    public JTextField remoteIP = new JTextField("127.0.0.1");

    private MenuStage() {
        super("MenuStage");
        this.add(title);
        this.add(newGame);
        this.add(findServer);
        this.add(load);
        this.add(rank);
        this.add(settings);
        this.add(quit);

        newGame.addActionListener((e) -> View.changeStage("RoomStage"));
        load.addActionListener((e) -> View.changeStage("LoadStage"));
        rank.addActionListener((e) -> View.changeStage("RankingStage"));
        settings.addActionListener((e) -> View.changeStage("SettingStage"));
        quit.addActionListener((e) -> View.window.dispose());

        findServer.addActionListener((e) -> {
            int result = JOptionPane.showConfirmDialog(this, new JComponent[] {
                    new JLabel("Name: "),
                    remoteName,
                    new JLabel("IP: "),
                    remoteIP

            }, "Set player", JOptionPane.DEFAULT_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (remoteName.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(MenuStage.instance(), "Name can't be empty!");
                    return;
                }
                if (Client.establishConnection(remoteIP.getText(), remoteName.getText())) {
                    Game.setOnlineType(OnlineType.CLIENT);
                    View.changeStage("RoomStage");
                } else {
                    JOptionPane.showMessageDialog(MenuStage.instance(), "连接失败");
                }
            }
        });
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
