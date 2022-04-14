package frame.view.components;

import javax.swing.*;
import java.awt.*;

public class BackgroundImagePanel extends JPanel {
    private Image image;
    public BackgroundImagePanel() {
        this(null);
    }

    public BackgroundImagePanel(Image background) {
        this.image = background;
        this.setOpaque(false);
    }

    public void setBackgroundImage(Image image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        }
    }
}
