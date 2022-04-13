package frame.view.stage;

import frame.Game;
import frame.board.BaseGrid;
import frame.board.BaseBoard;
import frame.event.EventCenter;
import frame.event.BoardChangeEvent;
import frame.player.PlayerType;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridView;
import frame.view.components.SaveDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameStage extends BaseStage {
    private static volatile GameStage sInstance = null;

    public final JPanel menuBar = new JPanel();
    public BoardView board;
    public final JPanel scoreBoard = new JPanel();

    public final JButton menuButton = new JButton("Menu");
    public final JButton undoButton = new JButton("Undo");
    public final JButton saveButton = new JButton("Save");

    public final SaveDialog saveDialog = new SaveDialog();

    public final ArrayList<GridView> grids = new ArrayList<>();

    private boolean enableClick = true;

    public void disableMouseClick() {
         enableClick = false;
    }

    public void enableMouseClick() {
         enableClick = true;
    }

    private GameStage() {
        super("GameStage");
        setLayout(new BorderLayout());

        menuButton.addActionListener((e) -> View.changeStage("MenuStage"));
        undoButton.addActionListener((e) -> Game.cancelLastAction());
        saveButton.addActionListener((e) -> saveDialog.dialog.setVisible(true));
        saveDialog.dialog.setLocationRelativeTo(this);

        drawComponents = () -> {
            menuBar.add(menuButton);
            menuBar.add(saveButton);
            menuBar.add(undoButton);
            this.add("North", menuBar);
            this.add("South", scoreBoard);
        };
    }


    @SuppressWarnings("unchecked")
    @Override
    public void enter() {

        Game.init();
        grids.clear();

        if (this.board != null)
            this.remove(this.board);
        this.board = View.boardViewFactory.createBoardView();
        this.add(this.board);

        BaseBoard board = Game.getBoard();
        GridBagConstraints gbc = new GridBagConstraints();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                BaseGrid baseGrid = board.getGrid(i, j);
                GridView gridView = View.gridViewFactory.createGridView(baseGrid);
                grids.add(gridView);
                Component gridViewComponent = (Component) gridView;
                int finalI = i;
                int finalJ = j;
                gridViewComponent.addMouseListener(new MouseAdapter() {
                    private boolean pressed = false;

                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        pressed = true;
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        super.mouseReleased(e);
                        if (enableClick && pressed && Game.getCurrentPlayer().getType() == PlayerType.LOCAL) {
                            Game.performAction(baseGrid.actionFactory.createAction(finalI, finalJ, e.getButton()));
                        }
                        pressed = false;
                    }
                });

                gbc.gridx = i;
                gbc.gridy = j;
                this.board.add(gridViewComponent, gbc);
                gridView.init();
            }
        }
        this.board.redraw();
        this.grids.forEach(GridView::redraw);
        this.revalidate();
        this.repaint();
        EventCenter.subscribe(BoardChangeEvent.class, (e) -> {
            this.board.redraw();
            this.grids.forEach(GridView::redraw);
            this.revalidate();
            this.repaint();
        });
    }

    public static GameStage instance() {
        if (sInstance == null) {
            synchronized (BaseStage.class) {
                if (sInstance == null) {
                    sInstance = new GameStage();
                }
            }
        }
        return sInstance;
    }
}
