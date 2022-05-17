package frame.view.board;

import frame.Controller.Game;
import frame.view.components.BackgroundImagePanel;

import javax.swing.*;
import java.awt.*;

public class BoardView extends JPanel {
    private Image image;

    public BoardView() {
        this(null);
    }

    public BoardView(Image background) {
        super(new GridBagLayout());
        image = background;
        this.setOpaque(false);
    }

    public void init() {
    }

    public void redraw() {
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            Dimension dim = getSize();
            int block = Math.min(dim.width / Game.getWidth(), dim.height / Game.getHeight());
            int width = block * Game.getWidth(), height = block * Game.getHeight();
            g.drawImage(image, (dim.width - width) / 2, (dim.height - height) / 2, width, height, null);
        }
    }
}
