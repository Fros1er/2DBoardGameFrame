package frame.controller;

import java.io.*;

public class Saver {
    private static volatile Saver sInstance = null;

    public Serializable load(String path) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(path));
            return (Serializable) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String save(Serializable obj, String path) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path));
            out.writeObject(obj);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Saver() {}

    public static Saver instance() {
        if (sInstance == null) {
            synchronized (Saver.class) {
                if (sInstance == null) {
                    sInstance = new Saver();
                }
            }
        }
        return sInstance;
    }
}
