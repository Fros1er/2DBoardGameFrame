package frame.save;

import frame.player.PlayerInfo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Saver {

    public static Save load(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            return (Save) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save(Save save, String path) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(save);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, PlayerInfo> loadPlayerInfo() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("players.sav"));
            return (Map<String, PlayerInfo>) in.readObject();
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
