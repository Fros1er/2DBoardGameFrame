package frame.player;

import frame.Controller.DefaultSaver;
import frame.event.EventCenter;
import frame.event.GameEndEvent;
import frame.event.PlayerChangeEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class PlayerManager {
    private static Player[] players = new Player[2];
    private static int currentPlayer = 0;
    private static final Map<String, PlayerInfo> playerInfoMap;

    static {
        playerInfoMap = DefaultSaver.loadPlayerInfo();
        EventCenter.subscribe(GameEndEvent.class, (e) -> updatePlayerInfo());
    }

    public static void nextPlayer() {
        for (int i = 1; i < players.length; i++) {
            if (!players[(currentPlayer + i) % players.length].isOut()) {
                currentPlayer = (currentPlayer + i) % players.length;
                break;
            }
        }
        players[currentPlayer].onNotify();
    }

    public static void previousPlayer() {
        for (int i = 1; i < players.length; i++) {
            if (!players[(players.length + currentPlayer - i) % players.length].isOut()) {
                currentPlayer = (players.length + currentPlayer - i) % players.length;
                break;
            }
        }
        players[currentPlayer].onNotify();
    }

    public static boolean isAllPlayerOut() {
        return Arrays.stream(players).allMatch(Player::isOut);
    }

    public static boolean isOnePlayerRemains() {
        return Arrays.stream(players).filter((player) -> !player.isOut()).count() == 1;
    }

    public static boolean isOnePlayerOut() {
        return Arrays.stream(players).filter((player) -> !player.isOut()).count() == 1;
    }

    public static boolean hasPlayerOut() {
        return Arrays.stream(players).anyMatch((player) -> !player.isOut());
    }

    public static boolean isPlayerRemains(int n) {
        return Arrays.stream(players).filter((player) -> !player.isOut()).count() == n;
    }

    public static boolean isPlayerWins(int n) {
        return Arrays.stream(players).filter((player) -> !player.isWin()).count() == n;
    }

    public static boolean isPlayerLoses(int n) {
        return Arrays.stream(players).filter((player) -> !player.isLose()).count() == n;
    }

    public static Player getCurrentPlayer() {
        return players[getCurrentPlayerIndex()];
    }

    public static int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    public static int getNextPlayerIndex() {
        for (int i = 1; i < players.length; i++) {
            if (!players[(currentPlayer + i) % players.length].isOut()) {
                return (currentPlayer + i) % players.length;
            }
        }
        return currentPlayer;
    }

    public static Player getPlayer(int i) {
        return players[i];
    }

    public static int getMaximumPlayerNumber() {
        return players.length;
    }

    public static void setMaximumPlayer(int maximumPlayer) {
        assert maximumPlayer > 0;
        Player[] players = new Player[maximumPlayer];
        System.arraycopy(PlayerManager.players, 0, players, 0, Math.min(maximumPlayer, PlayerManager.players.length));
        for (int i = 0; i < maximumPlayer; i++) {
            if (players[i] == null) players[i] = new LocalPlayer(i);
        }
        PlayerManager.players = players;
    }

    public static void setPlayer(int pos, PlayerType type, String name) {
        players[pos] = Player.playerFactory(pos, name, type);
        EventCenter.publish(new PlayerChangeEvent(players[pos], pos));
    }

    public static void reviveAllPlayers() {
        for (Player p : players) p.revive();
    }

    public static void reset() {
        reviveAllPlayers();
        currentPlayer = 0;
    }

    public static PlayerInfo getPlayerInfo(String name) {
        if (playerInfoMap.containsKey(name)) {
            return playerInfoMap.get(name);
        }
        PlayerInfo info = new PlayerInfo(name);
        playerInfoMap.put(name, info);
        return info;
    }

    public static Collection<PlayerInfo> getAllPlayersInfo() {
        return playerInfoMap.values();
    }

    private static void updatePlayerInfo() {
        for (Player player : players) {
            if (player.isWin()) playerInfoMap.get(player.getName()).addWinCount();
            else playerInfoMap.get(player.getName()).addLoseCount();
        }
        DefaultSaver.savePlayerInfo(playerInfoMap);
    }
}
