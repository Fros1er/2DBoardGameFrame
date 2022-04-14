package frame.save;

import java.io.IOException;

public abstract class Saver {

    public abstract Save getLoadedSave();

    public abstract boolean hasLoadedSave();

    public abstract void clearLoadedSave();

    /**
     * Load save from file.
     * @param path save to be loaded.
     * @throws ClassNotFoundException when deserialization fails.
     * @throws UnmatchedSizeException when checkSize is enabled and size doesn't match.
     */
    public abstract void load(String path) throws IOException, ClassNotFoundException, UnmatchedSizeException;

    public abstract void save(String path) throws IOException;

    public abstract void checkSize(boolean flag);
}
