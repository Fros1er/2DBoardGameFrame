package frame.view.components;

import frame.event.EventCenter;
import frame.event.PlayerChangeEvent;
import frame.player.*;
import frame.util.Procedure;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.function.Consumer;

public class RoomBlock extends BackgroundImagePanel {

    public final int id;
    protected static Consumer<RoomBlock> drawComponents = (RoomBlock roomBlock) -> {
        roomBlock.playerInfo.add(Box.createVerticalGlue());
        roomBlock.playerInfo.add(roomBlock.playerWinInfo);
        roomBlock.playerInfo.add(roomBlock.playerLoseInfo);
        roomBlock.playerInfo.add(Box.createVerticalGlue());

        roomBlock.add(roomBlock.playerIcon);
        roomBlock.add(roomBlock.playerName);
        roomBlock.add(roomBlock.playerInfo);
    };

    public JLabel playerIcon = new JLabel();
    public JLabel playerName = new JLabel();
    public Box playerInfo = new Box(1);
    public JLabel playerWinInfo = new JLabel();
    public JLabel playerLoseInfo = new JLabel();

    public JComboBox<PlayerType> playerTypeSetter = new JComboBox<>();
    public JTextField playerNameSetter = new JTextField();
    public JComboBox<String> aiNameChooser = new JComboBox<>();

    public RoomBlock(int id) {
        this.id = id;
        setBorder(new EmptyBorder(10, 50, 10, 10));
        setLayout(new GridLayout());

        playerIcon.setLayout(new BoxLayout(playerIcon, BoxLayout.Y_AXIS));

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

        playerTypeSetter.addActionListener((e) -> {
            if (Objects.requireNonNull(playerTypeSetter.getSelectedItem()) == PlayerType.LOCAL) {
                playerNameSetter.setVisible(true);
                aiNameChooser.setVisible(false);
            } else {
                playerNameSetter.setVisible(false);
                aiNameChooser.setVisible(true);
            }
        });

        bindPlayer();
        EventCenter.subscribe(PlayerChangeEvent.class, (e) -> {
            if (((PlayerChangeEvent) e).id == this.id) bindPlayer();
        });

        drawComponents.accept(this);
    }

    public void bindPlayer() {
        Player p = PlayerManager.getPlayer(id);
        playerName.setText(p.getName());
        PlayerInfo info = PlayerManager.getPlayerInfo(p.getName());
        if (!Objects.equals(info.getName(), "Waiting for player")) {
            playerWinInfo.setText("Win " + info.getWinCount());
            playerLoseInfo.setText("Lose " + info.getLoseCount());
        } else {
            playerWinInfo.setText("");
            playerLoseInfo.setText("");
        }
        ImageIcon icon;
        if (p.getType() == PlayerType.AI) {
            icon = new ImageIcon("src/main/resources/icons/AI.png");
        } else {
            icon = new ImageIcon("src/main/resources/icons/person.png");
        }
        playerIcon.setIcon(icon);

    }

    public void changePlayer() {
        int result = JOptionPane.showConfirmDialog(this, new JComponent[] {
                playerTypeSetter,
                new JLabel("Name: "),
                playerNameSetter,
                aiNameChooser
        }, "Set player", JOptionPane.DEFAULT_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name;
            if (Objects.requireNonNull(playerTypeSetter.getSelectedItem()) == PlayerType.AI) {
                name = (String) aiNameChooser.getSelectedItem();
            } else {
                name = playerNameSetter.getText();
            }
            PlayerManager.setPlayer(id, (PlayerType) playerTypeSetter.getSelectedItem(), name);
        }
    }
}