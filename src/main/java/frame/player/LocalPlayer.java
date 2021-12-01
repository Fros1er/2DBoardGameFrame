package frame.player;

public class LocalPlayer extends Player {

    public LocalPlayer(int id) {
        this(id, "Guest");
    }

    public LocalPlayer(int id, String name) {
        super(id, name, PlayerType.LOCAL );
    }
}
