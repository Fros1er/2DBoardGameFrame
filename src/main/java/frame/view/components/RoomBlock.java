package frame.view.components;

import frame.Game;
import frame.event.EventCenter;
import frame.event.PlayerChangeEvent;
import frame.player.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class RoomBlock extends JPanel {

    public final int id;

    public JLabel playerIcon = new JLabel();
    public JLabel playerName = new JLabel();
    public Box playerInfo = new Box(1);
    public JLabel playerWinInfo = new JLabel();
    public JLabel playerLoseInfo = new JLabel();
    public Box readyButtonBox = new Box(1);
    public JButton readyButton = new JButton("Ready");

    public JComboBox<PlayerType> playerTypeSetter = new JComboBox<>();
    public JTextField playerNameSetter = new JTextField();
    public JComboBox<String> aiNameChooser = new JComboBox<>();

    public RoomBlock(int id) {
        this.id = id;
        Player p = PlayerManager.getPlayer(id);
        setBorder(new EmptyBorder(10, 50, 10, 10));
        setLayout(new GridLayout());

        ImageIcon icon;
        switch (p.getType()) {
            case AI:
                icon = new ImageIcon("src/main/resources/icons/AI.png");
                break;
            case REMOTE:
                icon = new ImageIcon("src/main/resources/icons/internet.png");
                break;
            default:
                icon = new ImageIcon("src/main/resources/icons/person.png");
        }
        playerIcon.setIcon(icon);

        playerIcon.setLayout(new BoxLayout(playerIcon, BoxLayout.Y_AXIS));
        playerInfo.add(Box.createVerticalGlue());
        playerInfo.add(playerWinInfo);
        playerInfo.add(playerLoseInfo);
        playerInfo.add(Box.createVerticalGlue());

        readyButtonBox.add(Box.createVerticalGlue());
        readyButtonBox.add(readyButton);
        readyButtonBox.add(Box.createVerticalGlue());

        playerTypeSetter.addItem(PlayerType.LOCAL);
        playerTypeSetter.addItem(PlayerType.AI);


        for (String s : AIPlayer.getAllAINames()) aiNameChooser.addItem(s);
        aiNameChooser.setVisible(false);

        playerName.addMouseListener(new MouseAdapter() {
            private boolean pressed = false;

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                pressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (pressed) {
                    changePlayer();
                }
                pressed = false;
            }
        });

        readyButton.addActionListener((e) -> {
            p.setReady(true);
            readyButton.setEnabled(false);
        });

        playerTypeSetter.addActionListener((e) -> {
            switch ((PlayerType) Objects.requireNonNull(playerTypeSetter.getSelectedItem())) {
                case LOCAL:
                    playerNameSetter.setVisible(true);
                    aiNameChooser.setVisible(false);
                    break;
                case REMOTE:
                    playerNameSetter.setVisible(false);
                    aiNameChooser.setVisible(false);
                    break;
                case AI:
                    playerNameSetter.setVisible(false);
                    aiNameChooser.setVisible(true);
                    break;
            }
        });

        bindPlayer();
        EventCenter.subscribe(PlayerChangeEvent.class, (e) -> {
            if (((PlayerChangeEvent) e).id == this.id) bindPlayer();
        });

        add(playerIcon);
        add(playerName);
        add(playerInfo);
        add(readyButtonBox);
    }

    private void bindPlayer() {
        Player p = PlayerManager.getPlayer(id);
        playerName.setText(p.getName());
        readyButton.setEnabled(true);
        if (p.getType() == PlayerType.REMOTE) {
            readyButton.setEnabled(false);
        }
        PlayerInfo info = PlayerManager.getPlayerInfo(p.getName());
        if (!Objects.equals(info.getName(), "Waiting for player")) {
            playerWinInfo.setText("Win " + info.getWinCount());
            playerLoseInfo.setText("Lose " + info.getLoseCount());
        } else {
            playerWinInfo.setText("");
            playerLoseInfo.setText("");
        }

    }

    public void changePlayer() {
        if (Game.isClient()) return;
        playerTypeSetter.removeItem(PlayerType.REMOTE);
        if (Game.isServer()) {
            playerTypeSetter.addItem(PlayerType.REMOTE);
        }
        int result = JOptionPane.showConfirmDialog(this, new JComponent[] {
                playerTypeSetter,
                new JLabel("Name: "),
                playerNameSetter,
                aiNameChooser
        }, "Set player", JOptionPane.DEFAULT_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name;
            switch ((PlayerType) Objects.requireNonNull(playerTypeSetter.getSelectedItem())) {
                case AI:
                    name = (String) aiNameChooser.getSelectedItem();
                    break;
                case REMOTE:
                    name = "Waiting for player";
                    break;
                default:
                    name = playerNameSetter.getText();
            }
            PlayerManager.setPlayer(id, (PlayerType) playerTypeSetter.getSelectedItem(), name);
        }
    }
}