package util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Logger extends JPanel {
	public static final int LEVEL_MAX = 5;
	public static final int LEVEL_MIN = 0;
	public static int LEVEL_CURRENT = 3;
	private static JScrollPane scroll_pane;
	private static JTextArea text_area = new JTextArea();

	public Logger() {
		setLayout(new BorderLayout());
		scroll_pane = new JScrollPane(text_area);
		text_area.setEditable(false);
		add(scroll_pane, BorderLayout.CENTER);
		setPreferredSize(new Dimension(300, 200));
		appendNewLine("Logger initialized at: " + DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis())));
		log("Test", 0);
	}

	private void appendNewLine(String s) {
		text_area.append(s + "\n");
	}

	public static void log(String s, int level) {
		if (level >= LEVEL_CURRENT) {
			s = "[" + DateFormat.getTimeInstance().format(new Date(System.currentTimeMillis())) + "] " + Thread.currentThread().getStackTrace()[2] + ": " + s + "\n";
			text_area.append(s);
			text_area.setCaretPosition(text_area.getDocument().getLength());
		}
	}

	public void setLogLevel(int newLevel) {
		LEVEL_CURRENT = newLevel;
	}

	public static void showWindow() {
		JFrame frame = new JFrame();
		frame.add(new Logger(), BorderLayout.CENTER);
		frame.setBounds(100, 100, 300, 500);
		frame.setVisible(true);
	}
}
