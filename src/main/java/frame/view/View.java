package frame.view;

import frame.event.EventCenter;
import frame.event.GameEndEvent;
import frame.event.PlayerLoseEvent;
import frame.event.PlayerWinEvent;
import frame.player.Player;
import frame.player.PlayerManager;
import frame.view.board.BoardView;
import frame.view.board.BoardViewFactory;
import frame.view.board.GridViewFactory;
import frame.view.stage.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class View {

    //static class
    private View() {
    }

    //Display part
    public static final JFrame window = new JFrame();
    public static final CardLayout layout = new CardLayout();
    public static final JPanel sceneHolder = new JPanel(layout);

    private static final Map<String, BaseStage> stages = new HashMap<>();
    private static BaseStage currentStage;

    public static GridViewFactory gridViewFactory;
    public static BoardViewFactory boardViewFactory = BoardView::new;

    public static Consumer<Player> onPlayerWin = (Player player) -> {};
    public static Consumer<Player> onPlayerLose = (Player player) -> {};
    public static Consumer<Boolean> onGameEnd = (withdraw) -> {};

    static {
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(100, 100);
        window.setContentPane(sceneHolder);
        window.setVisible(true);

        addStage("MenuStage", MenuStage.instance());
        addStage("LoadStage", LoadStage.instance());
        addStage("RoomStage", RoomStage.instance());
        addStage("GameStage", GameStage.instance());
        addStage("RankingStage", RankingStage.instance());
        currentStage = MenuStage.instance();

    }

    public static void addStage(String name, BaseStage stage) {
        stages.put(name, stage);
        sceneHolder.add(stage, name);
    }

    public static BaseStage getStage(String name) {
        return stages.get(name);
    }

    public static void changeStage(String name) {
        if (!stages.containsKey(name)) {
            System.err.println("No stage found: " + name);
            return;
        }
        currentStage.exit();
        currentStage = stages.get(name);
        currentStage.enter();
        layout.show(sceneHolder, currentStage.getName());
    }

    public static void disableStage(String name, BaseStage jumpTo) {
        sceneHolder.remove(getStage(name));
        stages.put(name, jumpTo);
    }

    public static void setGridViewPattern(GridViewFactory factory) {
        gridViewFactory = factory;
    }

    public static void setBoardViewPattern(BoardViewFactory factory) {
        boardViewFactory = factory;
    }

    public static void setPlayerWinView(Consumer<Player> onPlayerWin) {
        View.onPlayerWin = onPlayerWin;
    }

    public static void setPlayerLoseView(Consumer<Player> onPlayerLose) {
        View.onPlayerLose = onPlayerLose;
    }

    public static void setGameEndView(Consumer<Boolean> onGameEnd) {
        View.onGameEnd = onGameEnd;
    }

    public static void setName(String name) {
        ((MenuStage) getStage("MenuStage")).title.setText(name);
        window.setTitle(name);
    }

    public static void start() {
        stages.values().forEach(BaseStage::init);

        layout.show(sceneHolder, "MenuStage");

        EventCenter.subscribe(PlayerWinEvent.class, (e) -> onPlayerWin.accept(((PlayerWinEvent) e).getPlayer()));
        EventCenter.subscribe(PlayerLoseEvent.class, (e) -> onPlayerLose.accept(((PlayerLoseEvent) e).getPlayer()));
        EventCenter.subscribe(GameEndEvent.class, (e) -> onGameEnd.accept(!PlayerManager.hasPlayerOut()));
    }
}
