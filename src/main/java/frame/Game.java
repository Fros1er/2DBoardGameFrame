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
import frame.socket.Client;
import frame.socket.OnlineType;
import frame.socket.Server;
import frame.util.Procedure;
import frame.view.stage.GameStage;

import java.lang.reflect.Constructor;
import java.util.Stack;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class Game {

    private static OnlineType onlineType = OnlineType.NONE;
    private static boolean isGameEnd = true;
    private static final Stack<Action> actionStack = new Stack<>();
    private static BaseBoard board;
    private static Constructor<? extends BaseBoard> boardConstructor;
    private static Predicate<Player> playerWinningJudge;
    private static Predicate<Player> playerLosingJudge;
    private static BooleanSupplier gameEndingJudge = PlayerManager::isOnePlayerWin;
    private static Procedure gameEndFunction = () -> {};
    private static Procedure initFunction = () -> {
    };
    private static Procedure gridActionRegister = () -> {};
    private static int width;
    private static int height;

    private static Save fromSave = null;

    public static final Object controllerSource = new Object();

    private Game() {
    }

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
                for (Action action : fromSave.actionStack) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    performAction(action);
                }
            });
            t.start();
        } else {
            PlayerManager.getCurrentPlayer().onNotify();
        }
        EventCenter.publish(new BoardChangeEvent(GameStage.instance()));
    }

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

    public static boolean performAction(Action action) {
        if (action == null) return false;

        if (isClient()) {
            Client.sendAction(action);
            return true;
        }

        if (!action.perform()) return false;
        Player currentPlayer = getCurrentPlayer();
        if (playerWinningJudge.test(currentPlayer)) {
            currentPlayer.win();
            action.setChangedPlayer(currentPlayer);
            EventCenter.publish(new PlayerWinEvent(action, currentPlayer));
        }
        if (playerLosingJudge.test(currentPlayer)) {
            currentPlayer.lose();
            action.setChangedPlayer(currentPlayer);
            EventCenter.publish(new PlayerLoseEvent(action, currentPlayer));
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

        if (isClient()) {
            Client.sendCancel();
            return;
        }

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

    public static void setOnlineType(OnlineType type) {
        onlineType = type;
        if (type == OnlineType.SERVER) {
            Server.startService();
        } else {
            Server.stopAllService();
        }
    }

    public static boolean isServer() {
        return onlineType == OnlineType.SERVER;
    }

    public static boolean isClient() {
        return onlineType == OnlineType.CLIENT;
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
        fromSave = Saver.load(path);
        setBoardSize(fromSave.width, fromSave.height);
    }

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
}
