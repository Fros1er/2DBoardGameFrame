package frame;

import frame.action.Action;
import frame.action.ActionFactory;
import frame.action.Range;
import frame.board.Board;
import frame.board.Grid;
import frame.event.EventCenter;
import frame.event.BoardChangeEvent;
import frame.event.PlayerLoseEvent;
import frame.event.PlayerWinEvent;
import frame.item.BaseItem;
import frame.player.Player;
import frame.util.Procedure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Game {

    private static int maximumPlayer = 2;
    private static boolean AIStatus = false;
    private static boolean onlineStatus = false;
    private static Player[] players = new Player[2];
    public static final List<BaseItem> items = new ArrayList<>();
    private static Player currentPlayer;

    private static Board board;
    private static Predicate<Player> playerWinningFunction;
    private static Predicate<Player> playerLosingFunction;
    private static Procedure initFunction = () -> {};

    private Game() {}

    public static void init() {
        initFunction.invoke();
    }

    public static void registerBoard(Board board) {
        Game.board = board;
        board.init();
    }

    public static Board getBoard() {
        return board;
    }

    public static <T extends Grid> void registerGridAction(Range range, ActionFactory<T> factory) {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                if (range.inRange(x, y)) {
                    board.getGrid(x, y).actionFactoryList.add(factory);
                }
            }
        }
    }

    public static void setPlayerWinningFunction(Predicate<Player> predicate) {
        playerWinningFunction = predicate;
    }
    public static void setPlayerLosingFunction(Predicate<Player> predicate) {
        playerLosingFunction = predicate;
    }

    public static void setInitFunction(Procedure procedure) {
        initFunction = procedure;
    }

    public static void performAction(Action action) {
        if (action == null) return;
        if (!action.perform()) return;

        if (playerWinningFunction.test(currentPlayer)) {
            EventCenter.publish(new PlayerWinEvent(action, currentPlayer));
        }
        if (playerLosingFunction.test(currentPlayer)) {
            EventCenter.publish(new PlayerLoseEvent(action, currentPlayer));
        }
        if (action.endTurn) nextTurn();

        EventCenter.publish(new BoardChangeEvent(action));
    }

    public static int getMaximumPlayerNumber() {
        return maximumPlayer;
    }

    public static Player getPlayer(int i) {
        return players[i];
    }
    public static void setPlayer(int pos, Player p) {
        players[pos] = p;
    }
    public static void nextTurn() {}

    public static void setMaximumPlayer(int maximumPlayer) {
        assert maximumPlayer > 0;
        Player[] players = new Player[maximumPlayer];
        System.arraycopy(Game.players, 0, players, 0, Math.min(maximumPlayer, Game.maximumPlayer));
        Game.maximumPlayer = maximumPlayer;
        Game.players = players;
    }

    public static void setAIStatus(boolean AIStatus) {
        Game.AIStatus = AIStatus;
    }

    public static boolean isAIEnabled() {
        return Game.AIStatus;
    }

    public static void setOnlineStatus(boolean onlineStatus) {
        Game.onlineStatus = onlineStatus;
    }

    public static boolean isOnlineEnabled() {
        return Game.onlineStatus;
    }
}
