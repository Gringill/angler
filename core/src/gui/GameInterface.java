package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class GameInterface extends JFrame {
	CardLayout cardLayout;
	JProgressBar progressBar;
	JList<String> messageList;
	JTextField sendText;
	JButton sendButton;
	JList<String> nameList;

	public GameInterface(String host) {
		super("Chat Client");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(640, 200);
		setLocationRelativeTo(null);

		Container contentPane = getContentPane();
		cardLayout = new CardLayout();
		contentPane.setLayout(cardLayout);
		{
			JPanel panel = new JPanel(new BorderLayout());
			contentPane.add(panel, "progress");
			panel.add(new JLabel("Connecting to " + host + "..."));
			{
				panel.add(progressBar = new JProgressBar(), BorderLayout.SOUTH);
				progressBar.setIndeterminate(true);
			}
		}
		{
			JPanel panel = new JPanel(new BorderLayout());
			contentPane.add(panel, "chat");
			{
				JPanel topPanel = new JPanel(new GridLayout(1, 2));
				panel.add(topPanel);
				{
					topPanel.add(new JScrollPane(
							messageList = new JList<String>()));
					messageList.setModel(new DefaultListModel<String>());
				}
				{
					topPanel.add(new JScrollPane(nameList = new JList<String>()));
					nameList.setModel(new DefaultListModel<String>());
				}
				DefaultListSelectionModel disableSelections = new DefaultListSelectionModel() {
					public void setSelectionInterval(int index0, int index1) {
					}
				};
				messageList.setSelectionModel(disableSelections);
				nameList.setSelectionModel(disableSelections);
			}
			{
				JPanel bottomPanel = new JPanel(new GridBagLayout());
				panel.add(bottomPanel, BorderLayout.SOUTH);
				bottomPanel.add(sendText = new JTextField(),
						new GridBagConstraints(0, 0, 1, 1, 1, 0,
								GridBagConstraints.CENTER,
								GridBagConstraints.BOTH,
								new Insets(0, 0, 0, 0), 0, 0));
				bottomPanel.add(sendButton = new JButton("Send"),
						new GridBagConstraints(1, 0, 1, 1, 0, 0,
								GridBagConstraints.CENTER, 0, new Insets(0, 0,
										0, 0), 0, 0));
			}
		}

		sendText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendButton.doClick();
			}
		});
	}

	public void setSendListener(final Runnable listener) {
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (getSendText().length() == 0)
					return;
				listener.run();
				sendText.setText("");
				sendText.requestFocus();
			}
		});
	}

	public void setCloseListener(final Runnable listener) {
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent evt) {
				listener.run();
			}

			public void windowActivated(WindowEvent evt) {
				sendText.requestFocus();
			}
		});
	}

	public String getSendText() {
		return sendText.getText().trim();
	}

	public void setNames(final String[] names) {
		// This listener is run on the client's update thread, which was
		// started by client.start().
		// We must be careful to only interact with Swing components on the
		// Swing event thread.
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				cardLayout.show(getContentPane(), "chat");
				DefaultListModel<String> model = (DefaultListModel<String>) nameList
						.getModel();
				model.removeAllElements();
				for (String name : names)
					model.addElement(name);
			}
		});
	}

	public void addMessage(final String message) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				DefaultListModel<String> model = (DefaultListModel<String>) messageList
						.getModel();
				model.addElement(message);
				messageList.ensureIndexIsVisible(model.size() - 1);
			}
		});
	}
}
