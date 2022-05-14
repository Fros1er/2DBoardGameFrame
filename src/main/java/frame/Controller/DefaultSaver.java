package frame.Controller;

import frame.player.PlayerInfo;
import frame.save.Save;
import frame.save.Saver;
import frame.save.UnmatchedSizeException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DefaultSaver extends Saver {

    private Save loadedSave = null;
    private boolean checkSize = false;

    public DefaultSaver() {

    }

    @Override
    public Save getLoadedSave() {
        return loadedSave;
    }

    @Override
    public boolean hasLoadedSave() {
        return loadedSave != null;
    }

    @Override
    public void clearLoadedSave() {
        loadedSave = null;
    }


    @Override
    public void save(String path) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
        out.writeObject(new Save(Game.boardClass, Game.actionStack));
        out.close();
    }

    @Override
    public void load(String path) throws IOException, ClassNotFoundException, UnmatchedSizeException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
        try {
            loadedSave = (Save) in.readObject();
            if (!loadedSave.boardClass.equals(Game.boardClass)) throw new ClassNotFoundException();
            if (checkSize && (loadedSave.height != Game.getHeight() || loadedSave.width != Game.getWidth()))
                throw new UnmatchedSizeException(Game.getWidth(), Game.getHeight(), loadedSave.width, loadedSave.height);
        } catch (ClassCastException ignored) {
            throw new ClassNotFoundException();
        }
    }

    @Override
    public void checkSize(boolean flag) {
        this.checkSize = flag;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, PlayerInfo> loadPlayerInfo() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("players.sav"));
            return (Map<String, PlayerInfo>) in.readObject();
        } catch (FileNotFoundException ignored) {

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public static void savePlayerInfo(Map<String, PlayerInfo> info) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("players.sav"));
            out.writeObject(info);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSaveInfo() {
        return "";//TODO
    }
}
