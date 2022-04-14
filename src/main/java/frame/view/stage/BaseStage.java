package frame.view.stage;

import frame.util.Procedure;
import frame.view.components.BackgroundImagePanel;
import frame.view.sound.AudioPlayer;

import java.util.concurrent.Future;

public abstract class BaseStage extends BackgroundImagePanel {
    private final String name;
    private String bgmPath = null;
    private Future<?> bgmThread = null;
    protected Procedure drawComponents;

    public BaseStage(String name) {
        this.name = name;
    }

    public void init() {
        drawComponents.invoke();
    }

    public void enter() {
        if (bgmPath != null)
            bgmThread = AudioPlayer.playBgm(bgmPath);
    }
    public void exit() {
        if (bgmThread != null)
            bgmThread.cancel(true);
        bgmThread = null;
    }

    public String getName() {
        return name;
    }

    public void setCustomDrawMethod(Procedure drawMethod) {
        drawComponents = drawMethod;
    }

    public void setBgm(String path) {
        bgmPath = path;
    }
}
