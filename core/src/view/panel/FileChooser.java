package view.panel;

import javax.swing.*;
import java.io.File;

/**
 * Wraps the standard JFileChooser for ease of use in cooperation with scene2d
 */
public class FileChooser {
    private static final int STILL_WAITING = -1337;
    public int option = STILL_WAITING;

    public static File instantiate() {
        FileChooser wrapper = new FileChooser();
        final JFileChooser fileChooser = new JFileChooser();

        new Thread(() -> {
            JFrame frame = new JFrame();
            wrapper.option = fileChooser.showDialog(frame, "Choose");
        }).start();


        while (wrapper.option == STILL_WAITING) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return fileChooser.getSelectedFile();
    }
}
