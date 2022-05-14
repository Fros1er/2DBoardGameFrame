package frame.Controller;

import frame.action.Action;
import frame.action.ActionFactory;
import frame.action.ActionPerformType;
import frame.action.Range;
import frame.board.BaseBoard;
import frame.event.*;
import frame.player.Player;
import frame.player.PlayerManager;
import frame.save.Saver;
import frame.util.Procedure;
import frame.view.View;
import frame.view.stage.GameStage;

import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Game {

    private static boolean isGameEnd = true;
    protected static final Stack<Action> actionStack = new Stack<>();
    private static BaseBoard board;
    protected static Class<? extends BaseBoard> boardClass;
    private static Predicate<Player> playerWinningJudge;
    private static Predicate<Player> playerLosingJudge = (player) -> false;
    private static BooleanSupplier gameEndingJudge = PlayerManager::hasPlayerOut;
    private static Consumer<Boolean> gameEndFunction = (withdraw) -> {
    };
    private static Procedure initFunction = () -> {
    };
    private static Procedure gridActionRegister = () -> {
    };
    private static int width;
    private static int height;
    private static boolean netWorkEnabled = false;
    public static Saver saver = new DefaultSaver();

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

    // Board methods
    public static void registerBoard(Class<? extends BaseBoard> board) {
        boardClass = board;
    }

    public static BaseBoard getBoard() {
        return board;
    }

    // GamePlay

    public static void registerGridAction(Range range, ActionFactory factory) {
        gridActionRegister = () -> board.forEach((p, grid) -> {
            if (range.inRange(p.x, p.y)) {
                if (grid.actionFactory != null) {
                    System.err.println("Warning: Illegally overriding action in grid " + p.x + " " + p.y);
                }
                grid.actionFactory = factory;
            }
        });
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

    public static void setGameEndFunction(Consumer<Boolean> procedure) {
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

    public static int getNextPlayerIndex() {
        return PlayerManager.getNextPlayerIndex();
    }


    static Future<?> loadingSave = null;

    /**
     * Reset gameplay and board.
     */
    public static void init() {
        if (loadingSave != null && !loadingSave.isDone() && !loadingSave.isCancelled()) {
            loadingSave.cancel(true);
            saver.clearLoadedSave();
        }
        isGameEnd = false;
        actionStack.clear();
        try {
            board = boardClass.getConstructor(int.class, int.class).newInstance(width, height);
            board.init();
            gridActionRegister.invoke();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        PlayerManager.reset();
        initFunction.invoke();
        if (saver.hasLoadedSave()) {
            loadingSave = Executors.newSingleThreadExecutor().submit(() -> {
                ((GameStage) View.getStage("GameStage")).disableMouseClick();
                for (Action action : saver.getLoadedSave().actionStack) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!performAction(action)) {
                        EventCenter.publish(new InvalidLoadEvent(action));
                        return;
                    }
                }
                ((GameStage) View.getStage("GameStage")).enableMouseClick();
                saver.clearLoadedSave();
            });
        } else {
            PlayerManager.getCurrentPlayer().onNotify();
        }
        EventCenter.publish(new BoardChangeEvent(View.getStage("GameStage")));
    }

    private static void removePendingActions() {
        if (!actionStack.isEmpty()) {
            Action lastAction = actionStack.peek();
            while (lastAction.type == ActionPerformType.PENDING) {
                lastAction.removePending();
                actionStack.pop();
                if (actionStack.isEmpty()) break;
                lastAction = actionStack.peek();
            }
        }
    }

    public static boolean performAction(Action action) {
        if (action == null || isGameEnd) return false;
        ActionPerformType type = action.perform();
        action.type = type;
        switch (type) {
            case SUCCESS:
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
                    gameEndFunction.accept(PlayerManager.hasPlayerOut());
                    EventCenter.publish(new GameEndEvent(action));
                }
                if (action.endTurn && !isGameEnd) nextTurn();
            case PENDING:
                actionStack.push(action);
                EventCenter.publish(new BoardChangeEvent(action));
                return true;
            case FAIL:
                removePendingActions();
                return false;
        }
        return true;
    }

    public static void cancelLastAction() {
        if (actionStack.isEmpty() || isGameEnd) return;

        // If top of the stack is a succeed action, cancel.
        Action lastAction = actionStack.peek();
        if (lastAction.type == ActionPerformType.SUCCESS) {
            lastAction.undo();
            actionStack.pop();
            if (lastAction.endTurn) previousTurn();
        }

        // Remove all pending actions.
        removePendingActions();

        EventCenter.publish(new BoardChangeEvent(lastAction));
    }

    public static void nextTurn() {
        PlayerManager.nextPlayer();
    }

    public static void previousTurn() {
        PlayerManager.previousPlayer();
    }

    public static void enableNetwork() {
        netWorkEnabled = true;
    }

    public static void disableNetwork() {
        netWorkEnabled = false;
    }
}
