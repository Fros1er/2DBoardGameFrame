package frame.player;

import java.util.HashMap;
import java.util.Map;

public abstract class Player {
    private final int id;
    private final String name;
    private boolean isReady;
    protected final String type;
    public static final Map<String, Class<?>> playerTypes = new HashMap<>();

    static {
        addPlayerType("LOCAL", LocalPlayer.class);
        addPlayerType("AI", AIPlayer.class);
        addPlayerType("REMOTE", RemotePlayer.class);
    }

    public Player(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public void onNotify() {}

    public int getId() {
        return id;
    }

    public void setReady(boolean state) {
        isReady = state;
    }

    public boolean isReady() { return isReady; }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public static Player getPlayer(String type, int id, String name) {
        Class<?> cls = playerTypes.get(type);
        Player p = null;
        try {
             p = (Player) (cls.getDeclaredConstructor(int.class, String.class).newInstance(id, name));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public static void addPlayerType(String type, Class<? extends Player> cls) {
        playerTypes.put(type, cls);
    }

    public static void removePlayerType(String type) {
        playerTypes.remove(type);
    }
}
