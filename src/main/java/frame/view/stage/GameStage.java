package frame.view.stage;

import frame.Controller.Game;
import frame.board.BaseBoard;
import frame.board.BaseGrid;
import frame.event.BoardChangeEvent;
import frame.event.EventCenter;
import frame.event.InvalidLoadEvent;
import frame.player.PlayerType;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridView;
import frame.view.components.BackgroundImagePanel;
import frame.view.components.SaveDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.function.Consumer;

public class GameStage extends BaseStage {
    private static volatile GameStage sInstance = null;

    public final BackgroundImagePanel menuBar = new BackgroundImagePanel();
    public BoardView board;
    public final BackgroundImagePanel scoreBoard = new BackgroundImagePanel();

    public final JButton menuButton = new JButton("Menu");
    public final JButton undoButton = new JButton("Undo");
    public final JButton saveButton = new JButton("Save");
    public final JButton surrenderButton = new JButton("Surrender");

    public SaveDialog saveDialog = null;

    private GridView[][] grids;

    private boolean enableClick = true;

    public void disableMouseClick() {
        enableClick = false;
    }

    public void enableMouseClick() {
        enableClick = true;
    }

    public Consumer<EventObject> invalidLoadCallback = (e) -> {
        JOptionPane.showMessageDialog(this, "Load failed: Invalid action.");
        View.changeStage("MenuStage");
    };

    private GameStage() {
        super("GameStage");
        setLayout(new BorderLayout());

        menuButton.addActionListener((e) -> View.changeStage("MenuStage"));
        undoButton.addActionListener((e) -> Game.cancelLastAction());
        surrenderButton.addActionListener(e -> Game.getCurrentPlayer().surrender());

        drawComponents = () -> {
            menuBar.add(menuButton);
            menuBar.add(saveButton);
            menuBar.add(undoButton);
            this.add("North", menuBar);
            this.add("South", scoreBoard);
        };
    }

    @Override
    public void init() {
        super.init();
        saveDialog = new SaveDialog();
        saveButton.addActionListener((e) -> saveDialog.dialog.setVisible(true));
        saveDialog.dialog.setLocationRelativeTo(this);
        EventCenter.subscribe(InvalidLoadEvent.class, invalidLoadCallback);
    }


    @Override
    public void enter() {

        Game.init();
        grids = new GridView[Game.getBoard().getWidth()][Game.getBoard().getHeight()];

        if (this.board != null)
            this.remove(this.board);
        this.board = View.boardViewFactory.createBoardView();
        this.add(this.board);

        BaseBoard board = Game.getBoard();
        GridBagConstraints gbc = new GridBagConstraints();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                BaseGrid baseGrid = board.getGrid(i, j);
                GridView gridView = View.gridViewFactory.createGridView();
                grids[i][j] = gridView;
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
        redrawBoard();
        EventCenter.subscribe(BoardChangeEvent.class, (e) -> redrawBoard());
    }


    private void redrawBoard() {
        if (this.grids[0][0] == null) return;
        this.board.redraw();
        for (int i = 0; i < Game.getWidth(); i++) {
            for (int j = 0; j < Game.getHeight(); j++) {
                this.grids[i][j].redraw(Game.getBoard().getGrid(i, j));
            }
        }
        this.revalidate();
        this.repaint();
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
