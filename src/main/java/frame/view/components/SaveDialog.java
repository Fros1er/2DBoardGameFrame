package frame.view.components;

import frame.Controller.Game;
import frame.save.UnmatchedSizeException;
import frame.view.View;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class SaveDialog {

    public final JDialog dialog = new JDialog(View.window, "Select Slot", true);
    public final JLabel headLabel = new JLabel("Please select slot");
    public static final BackgroundImagePanel[] slotPanels = new BackgroundImagePanel[Game.saver.getSlotNumber()];
    public static final JRadioButton[] radioButtons = new JRadioButton[Game.saver.getSlotNumber()];

    public final BackgroundImagePanel buttonPanel = new BackgroundImagePanel();
    public final JButton confirmButton = new JButton("Save");
    public final JButton cancelButton = new JButton("Cancel");

    public final JButton fileChooserButton = new JButton("Select file");

    public JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

    public SaveDialog() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("save", ".sav"));
        fileChooserButton.addActionListener((e) -> {
            int res = fileChooser.showOpenDialog(dialog);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Game.saver.save(file.getPath());
                    dialog.setVisible(false);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Failed to save: " + ex.getMessage());
                }
            }
        });

        dialog.setSize(256, 384);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.add(headLabel);
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < Game.saver.getSlotNumber(); i++) {
            BackgroundImagePanel panel = new BackgroundImagePanel();
            JRadioButton select = new JRadioButton();
            radioButtons[i] = select;
            group.add(select);
            panel.add(new JLabel("Slot " + (i + 1)));
            panel.add(select);
            panel.addMouseListener(new MouseAdapter() {
                private boolean pressed = false;
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    pressed = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    if (pressed) {
                        select.doClick();
                    }
                    pressed = false;
                }
            });
            slotPanels[i] = panel;
            dialog.add(panel);
        }
        dialog.add(fileChooserButton);

        cancelButton.addActionListener((e) -> dialog.setVisible(false));
        confirmButton.addActionListener((e) -> {
            for (int i = 0; i < radioButtons.length; i++) {
                if (radioButtons[i].isSelected()) {
                    try {
                        Game.saver.save(String.format("./saves/save%d.sav", i + 1));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(dialog, "Failed to save: " + ex.getMessage());
                    }
                    dialog.setVisible(false);
                    break;
                }
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        dialog.add(buttonPanel);
    }
}
