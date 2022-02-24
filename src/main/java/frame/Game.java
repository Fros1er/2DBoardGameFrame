package frame;

import frame.action.Action;
import frame.action.ActionFactory;
import frame.action.Range;
import frame.action.SurrenderAction;
import frame.board.BaseBoard;
import frame.player.PlayerManager;
import frame.save.Save;
import frame.save.Saver;
import frame.event.*;
import frame.player.Player;
import frame.util.Procedure;
import frame.view.View;
import frame.view.stage.GameStage;

import java.lang.reflect.Constructor;
import java.util.Stack;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class Game {

    private static boolean isGameEnd = true;
    private static final Stack<Action> actionStack = new Stack<>();
    private static BaseBoard board;
    private static Constructor<? extends BaseBoard> boardConstructor;
    private static Predicate<Player> playerWinningJudge;
    private static Predicate<Player> playerLosingJudge = (player) -> false;
    private static BooleanSupplier gameEndingJudge = PlayerManager::isOnePlayerWin;
    private static Procedure gameEndFunction = () -> {
    };
    private static Procedure initFunction = () -> {
    };
    private static Procedure gridActionRegister = () -> {
    };
    private static int width;
    private static int height;
    private static Save fromSave = null;
    private static int slotNumber = 3;

    public static final Object controllerSource = new Object();

    private Game() {
    }

    // properties

    public static void setBoardSize(int width, int height) {
        Game.width = width;
        Game.height = height;
    }

    public static int getHeight() {
        return height;
    }

    public static int getWidth() {
        return width;
    }

    public static int getSlotNumber() {
        return slotNumber;
    }

    public static void setSlotNumber(int slotNumber) {
        Game.slotNumber = slotNumber;
    }

    // Board methods
    public static void registerBoard(Class<? extends BaseBoard> board) {
        try {
            boardConstructor = board.getConstructor(int.class, int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("Class doesn't extended BaseBoard!");
        }
    }

    public static BaseBoard getBoard() {
        return board;
    }

    // GamePlay

    public static void registerGridAction(Range range, ActionFactory factory) {
        gridActionRegister = () -> {
            for (int x = 0; x < board.getWidth(); x++) {
                for (int y = 0; y < board.getHeight(); y++) {
                    if (range.inRange(x, y)) {
                        if (board.getGrid(x, y).actionFactory != null) {
                            System.err.println("Warning: Illegally overriding action in grid " + x + " " + y);
                        }
                        board.getGrid(x, y).actionFactory = factory;
                    }
                }
            }
        };
    }

    public static void setPlayerWinningJudge(Predicate<Player> predicate) {
        playerWinningJudge = predicate;
    }

    public static void setPlayerLosingJudge(Predicate<Player> predicate) {
        playerLosingJudge = predicate;
    }

    public static void setGameEndingJudge(BooleanSupplier supplier) {
        gameEndingJudge = supplier;
    }

    public static void setInitFunction(Procedure procedure) {
        initFunction = procedure;
    }

    public static void setGameEndFunction(Procedure procedure) {
        gameEndFunction = procedure;
    }

    //PlayerManager proxies
    public static void setMaximumPlayer(int maximumPlayer) {
        PlayerManager.setMaximumPlayer(maximumPlayer);
    }

    public static int getMaximumPlayerNumber() {
        return PlayerManager.getMaximumPlayerNumber();
    }

    public static Player getCurrentPlayer() {
        return PlayerManager.getCurrentPlayer();
    }

    public static int getCurrentPlayerIndex() {
        return PlayerManager.getCurrentPlayerIndex();
    }

    //Saver proxies

    public static void saveGame(String path) {
        Saver.save(new Save(actionStack), path);
    }

    public static void loadGame(String path) {
        System.out.println(path);
        fromSave = Saver.load(path);
        assert fromSave != null;
        setBoardSize(fromSave.width, fromSave.height);
    }

    // Not allowed methods
    public static void init() {
        isGameEnd = false;
        try {
            board = boardConstructor.newInstance(width, height);
            board.init();
            gridActionRegister.invoke();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        PlayerManager.reviveAllPlayers();
        initFunction.invoke();
        if (fromSave != null) {
            Thread t = new Thread(() -> {
                ((GameStage) View.getStage("GameStage")).disableMouseClick();
                for (Action action : fromSave.actionStack) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    performAction(action);
                }
                ((GameStage) View.getStage("GameStage")).enableMouseClick();
            });
            t.start();
        } else {
            PlayerManager.getCurrentPlayer().onNotify();
        }
        EventCenter.publish(new BoardChangeEvent(View.getStage("GameStage")));
    }

    public static boolean performAction(Action action) {
        if (action == null) return false;
        if (!action.perform()) return false;
        for (int i = 0; i < getMaximumPlayerNumber(); i++) {
            Player player = PlayerManager.getPlayer(i);
            if (playerWinningJudge.test(player)) {
                player.win();
                action.setChangedPlayer(player);
                EventCenter.publish(new PlayerWinEvent(action, player));
            }
            if (playerLosingJudge.test(player)) {
                player.lose();
                action.setChangedPlayer(player);
                EventCenter.publish(new PlayerLoseEvent(action, player));
            }
        }

        if (gameEndingJudge.getAsBoolean()) {
            isGameEnd = true;
            gameEndFunction.invoke();
            EventCenter.publish(new GameEndEvent(action));
        }
        actionStack.push(action);
        EventCenter.publish(new BoardChangeEvent(action));
        if (action.endTurn && !isGameEnd) nextTurn();
        return true;
    }

    public static void cancelLastAction() {
        if (actionStack.isEmpty() || isGameEnd) return;
        Action lastAction = actionStack.pop();
        lastAction.undo();
        while ((lastAction instanceof SurrenderAction) && !actionStack.isEmpty()) {
            lastAction.undo();
            if (lastAction.getChangedPlayer() != null) {
                lastAction.getChangedPlayer().revive();
            }
            lastAction = actionStack.pop();
        }
        if (lastAction.endTurn) previousTurn();
        EventCenter.publish(new BoardChangeEvent(lastAction));
    }

    public static void nextTurn() {
        PlayerManager.nextPlayer();
    }

    public static void previousTurn() {
        PlayerManager.previousPlayer();
    }
}
