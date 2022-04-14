package frame.view.stage;

import frame.player.PlayerInfo;
import frame.player.PlayerManager;
import frame.view.View;
import frame.view.components.BackgroundImagePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;

public class RankingStage extends BaseStage {
    private static volatile RankingStage sInstance = null;

    public final JLabel title = new JLabel("Rank", JLabel.CENTER);
    public final BackgroundImagePanel titlePanel = new BackgroundImagePanel();
    public final BackgroundImagePanel buttonPanel = new BackgroundImagePanel();
    public final JTable rankingTable;
    public final JScrollPane rankingPanel;
    public final JButton back = new JButton("Back");

    private RankingStage() {
        super("RankingStage");
        setLayout(new BorderLayout());
        back.addActionListener((e) -> View.changeStage("MenuStage"));
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Name");
        model.addColumn("Win");
        model.addColumn("Lose");
        rankingTable = new JTable(model);
        rankingPanel = new JScrollPane(rankingTable);
        rankingTable.setEnabled(false);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 3; i++) {
            rankingTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        drawComponents = () -> {
            this.add("North", titlePanel);
            this.add("South", buttonPanel);
            this.add(rankingPanel);
            titlePanel.add(title);
            buttonPanel.add(back);
        };
    }

    @Override
    public void enter() {
        PlayerManager.getAllPlayersInfo().stream()
                .sorted(Comparator.comparingInt(PlayerInfo::getWinCount))
                .forEach((p) -> ((DefaultTableModel) rankingTable.getModel()).addRow(new Object[]{p.getName(), p.getWinCount(), p.getLoseCount()}));
    }

    public static RankingStage instance() {
        if (sInstance == null) {
            synchronized (BaseStage.class) {
                if (sInstance == null) {
                    sInstance = new RankingStage();
                }
            }
        }
        return sInstance;
    }
}
