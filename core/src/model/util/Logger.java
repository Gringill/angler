package model.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;

public class Logger extends JPanel {
    private static Thread thread;
    private static JScrollPane scroll_pane;
    private static JTextArea text_area = new JTextArea();
    private static ArrayList<LogLine> lines = new ArrayList();
    private static ArrayList<String> filter = new ArrayList();
    private static ArrayList<LogLine> deltaLines = new ArrayList();
    private static JFrame frame;
    private static JTextField filter_search;

    public Logger() {
        thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    process();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        setLayout(new BorderLayout());
        scroll_pane = new JScrollPane(text_area);
        text_area.setEditable(false);
        add(scroll_pane, BorderLayout.CENTER);
        setPreferredSize(new Dimension(300, 200));

    }

    public static void log(String text, String tag, boolean scrollToLine) {
        LogLine line = new LogLine(new Date(System.currentTimeMillis()), text, tag, Thread.currentThread().getStackTrace());
        lines.add(line);
        deltaLines.add(line);
    }

    public static void applyFilter(ArrayList<String> filter) {
        Logger.filter = filter;
        text_area.setText("");
        for (LogLine line : lines) {
            if (filter.get(0).equals("*") || filter.contains(line.tag)) {
                text_area.append(line.toString() + "\n");
            }
        }
    }

    public static void showWindow() {
        frame = new JFrame();
        filter_search = new JTextField("*");
        frame.add(filter_search);
        KeyEventDispatcher ked = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        filter.clear();
                        for (String filterItem : filter_search.getText().split(", ")) {
                            filter.add(filterItem);
                        }
                        applyFilter(filter);
                        break;
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(ked);
        frame.add(filter_search, BorderLayout.NORTH);
        frame.add(new Logger(), BorderLayout.CENTER);
        frame.setBounds(100, 100, 300, 500);
        frame.setVisible(true);
    }

    public void process() {
        ArrayList<LogLine> lines = (ArrayList) deltaLines.clone();
        deltaLines.clear();
        lines.stream().filter(line -> (filter.size() == 0 || filter.contains(line.tag))).forEach(line -> text_area.append(line.toString() + "\n"));
    }
}
