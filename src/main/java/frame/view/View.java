package frame.view;

import frame.board.Grid;
import frame.event.EventCenter;
import frame.controller.Saver;
import frame.event.GameEndEvent;
import frame.event.PlayerLoseEvent;
import frame.event.PlayerWinEvent;
import frame.player.Player;
import frame.util.Procedure;
import frame.view.board.BoardViewFactory;
import frame.view.board.GridViewFactory;
import frame.view.stage.*;
import javafx.scene.media.MediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class View {

    public static String title = "";

    //static class
    private View() {
    }

    //Display part
    public static final JFrame window = new JFrame();
    public static final CardLayout layout = new CardLayout();
    public static final JPanel sceneHolder = new JPanel(layout);
    public static MediaPlayer bgm = null;

    private static final Map<String, BaseStage> stages = new HashMap<>();
    private static BaseStage currentStage = null;

    public static Saver saver = Saver.instance();
    public static GridViewFactory gridViewFactory;
    public static BoardViewFactory boardViewFactory;

    public static Consumer<Player> onPlayerWin = (Player player) -> {};
    public static Consumer<Player> onPlayerLose = (Player player) -> {};
    public static Procedure onGameEnd = () -> {};

    static {
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setSize(100, 100);
        window.setContentPane(sceneHolder);
        window.setVisible(true);

        addStage("MenuStage", MenuStage.instance());
        addStage("LoadStage", LoadStage.instance());
        addStage("RoomStage", RoomStage.instance());
        addStage("GameStage", GameStage.instance());

        layout.show(sceneHolder, "MenuStage");
        currentStage = MenuStage.instance();

    }

    public static void setName(String name) {
        title = name;
        MenuStage.instance().title.setText(name);
        window.setTitle(name);
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
            System.out.println("No stage found: " + name);
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

    public static void setMusic(String path) {
        //bgm = new MediaPlayer(new Media(path));
    }

    public static <T extends Grid> void setGridViewPattern(GridViewFactory<T> factory) {
        gridViewFactory = factory;
    }

    public static void setBoardViewPattern(BoardViewFactory factory) {
        boardViewFactory = factory;
    }

    /**
     * Add a JFrame to GameStage, using to import a demo or a completed game to frame.
     */
    public static void importFrame(JFrame frame) {
        window.setJMenuBar(frame.getJMenuBar());
        GameStage.instance().board.add(frame.getContentPane());
        frame.dispose();
    }

    public static void start() {
        MenuStage.instance().init();
        LoadStage.instance().init();
        RoomStage.instance().init();
        GameStage.instance().init();

        EventCenter.subscribe(PlayerWinEvent.class, (e) -> onPlayerWin.accept(((PlayerWinEvent) e).getPlayer()));
        EventCenter.subscribe(PlayerLoseEvent.class, (e) -> onPlayerLose.accept(((PlayerLoseEvent) e).getPlayer()));
        EventCenter.subscribe(GameEndEvent.class, (e) -> onGameEnd.invoke());
    }

    public static void setPlayerWinView(Consumer<Player> onPlayerWin) {
        View.onPlayerWin = onPlayerWin;
    }

    public static void setPlayerLoseView(Consumer<Player> onPlayerLose) {
        View.onPlayerLose = onPlayerLose;
    }

    public static void setGameEndView(Procedure onGameEnd) {
        View.onGameEnd = onGameEnd;
    }
}
