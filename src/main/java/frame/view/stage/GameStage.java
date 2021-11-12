package frame.view.stage;

import frame.Game;
import frame.action.ActionFactory;
import frame.board.Board;
import frame.board.Grid;
import frame.event.EventCenter;
import frame.event.BoardChangeEvent;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameStage extends BaseStage {
    private static volatile GameStage sInstance = null;

    public final JPanel menuBar = new JPanel();
    public BoardView board;
    public final JPanel itemBar = new JPanel();
    public final JPanel scoreBoard = new JPanel();

    public final ArrayList<GridView> grids = new ArrayList<>();


    private GameStage() {
        super("GameStage");
        this.add(menuBar);
        this.add(scoreBoard);
    }

    @Override
    public void init() {
        Board board = Game.getBoard();
        this.board = View.boardViewFactory.createBoardView();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                Grid grid = board.getGrid(i, j);
                GridView gridView = View.gridViewFactory.createGridView(grid);
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
                        if (pressed) {
                            for (ActionFactory factory : grid.actionFactoryList) {
                                Game.performAction(factory.createAction(Game.getBoard().getGrid(finalI, finalJ), finalI, finalJ, e.getButton()));
                            }
                        }
                        pressed = false;
                    }
                });
                this.board.add(gridViewComponent);
                gridView.init();
                gridView.redraw();
            }
        }
        this.add(this.board);
        this.board.redraw();
        if (!Game.items.isEmpty()) this.add(itemBar);

        EventCenter.subscribe(BoardChangeEvent.class, (e) -> {
            this.board.redraw();
            grids.forEach(GridView::redraw);
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
