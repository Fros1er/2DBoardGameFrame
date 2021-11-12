package frame.view.stage;

import frame.Game;
import frame.player.Player;
import frame.view.View;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class RoomStage extends BaseStage {
    private static volatile RoomStage sInstance = null;

    public RoomBlock[] roomBlocks = null;
    public final JButton back = new JButton("Back");
    public final JButton start = new JButton("Start");
    public final JCheckBox online = new JCheckBox("Allow LAN");

    private RoomStage() {
        super("RoomStage");
        online.addItemListener((e) -> Game.setOnlineStatus(e.getStateChange() == ItemEvent.SELECTED));
        back.addActionListener((e) -> View.changeStage("MenuStage"));
        start.addActionListener((e) -> View.changeStage("GameStage"));
        this.add(online);
        this.add(back);
        this.add(start);
    }

    @Override
    public void init() {
        roomBlocks = new RoomBlock[Game.getMaximumPlayerNumber()];
        for(int i = 0; i < Game.getMaximumPlayerNumber(); i++) {
            roomBlocks[i] = new RoomBlock(i);
            this.add(roomBlocks[i]);
        }
    }

    @Override
    public void enter() {
        for (int i = 0; i < Game.getMaximumPlayerNumber(); i++) {
            roomBlocks[i].usePlayer(Game.getPlayer(i));
        }
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

    public static class RoomBlock extends JPanel {

        public final int id;
        private Player p;

        public JPanel normalPanel = new JPanel();
        public JLabel prefix = new JLabel("");
        public JLabel title;
        public JLabel name = new JLabel("");
        public JCheckBox ready = new JCheckBox("Ready?");

        public JPanel createPanel = new JPanel();
        public JTextField newPlayerName = new JTextField(16);
        public JComboBox<String> newPlayerType;
        public JButton create = new JButton("New Player");

        public RoomBlock(int id) {
            this.id = id;
            title = new JLabel("Player " + id);
            ready.addItemListener((e) -> {
                p.setReady(e.getStateChange() == ItemEvent.SELECTED);
            });

            normalPanel.add(prefix);
            normalPanel.add(title);
            normalPanel.add(name);
            normalPanel.add(ready);


            newPlayerType = new JComboBox<>(Player.playerTypes.keySet().toArray(new String[0]));
            create.addActionListener((e) -> {
                p = Player.getPlayer((String) newPlayerType.getSelectedItem(), id, newPlayerName.getText());
                Game.setPlayer(id, p);
                usePlayer(p);
            });

            createPanel.add(newPlayerName);
            createPanel.add(newPlayerType);
            createPanel.add(create);

            this.add(normalPanel);
            this.add(createPanel);
        }

        public void usePlayer(Player p) {

            if (p == null) {
                normalPanel.setVisible(false);
                createPanel.setVisible(true);
            } else {
                createPanel.setVisible(false);
                normalPanel.setVisible(true);
                prefix.setText(p.getType());
                ready.setSelected(p.isReady());
                if (!p.getType().equals("LOCAL")) {
                    ready.setEnabled(false);
                }
                name.setText(p.getName());
            }
            this.p = p;
        }
    }
}
