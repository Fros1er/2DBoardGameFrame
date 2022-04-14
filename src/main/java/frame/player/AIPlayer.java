package frame.player;

import frame.Controller.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.*;

public abstract class AIPlayer extends Player {

    private final int delay;

    public AIPlayer(int id, String name, int delay) {
        super(id, name, PlayerType.AI);
        this.delay = delay;
    }

    @Override
    public void onNotify() {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!calculateNextMove()) {
                surrender();
            }
        });
        t.start();
    }

    protected boolean performGridAction(int x, int y, int button) {
        return Game.performAction(Game.getBoard().getGrid(x, y).actionFactory.createAction(x, y, button));
    }

    protected abstract boolean calculateNextMove();

    private static final Map<String, Function<Integer, AIPlayer>> aiFactories = new HashMap<>();

    public static void addAIType(String name, Function<Integer, AIPlayer> aiFactory) {
        aiFactories.put(name, aiFactory);
    }

    public static AIPlayer getAIPlayer(int id, String name) {
        System.out.println(name);
        return aiFactories.get(name).apply(id);
    }

    public static Set<String> getAllAINames() {
        return aiFactories.keySet();
    }

}