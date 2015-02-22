package controller;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

// This class is a convenient place to keep things common to both the client and server.
public class Net {
	public static final int port = 54555;
	private static final String SERVER_IP = "54.191.166.71";
	public static Client CLIENT;
	private static String sessionID;

	// This registers objects that are going to be sent over the network.
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(RegisterName.class);
		kryo.register(String[].class);
		kryo.register(UpdateNames.class);
		kryo.register(ChatMessage.class);
		if (endPoint instanceof Client) {
			defineClientNetListener((Client) endPoint);
		}
	}

	private static void defineClientNetListener(final Client client) {
		client.addListener(new Listener() {
			public void connected(Connection connection) {
				// RegisterName registerName = new RegisterName();
				// registerName.name = name;
				// client.sendTCP(registerName);
			}

			public void received(Connection connection, Object object) {
				if (object instanceof UpdateNames) {
					// UpdateNames updateNames = (UpdateNames) object;
					// chatFrame.setNames(updateNames.names);
					return;
				}

				if (object instanceof ChatMessage) {
					// ChatMessage chatMessage = (ChatMessage) object;
					// chatFrame.addMessage(chatMessage.text);
					return;
				}
			}

			public void disconnected(Connection connection) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						// Closing the frame calls the close listener which will
						// stop the client's update thread.
						// chatFrame.dispose();
					}
				});
			}
		});
	}

	public static void setupGameClient() {
		CLIENT = new Client();
		CLIENT.start();
		register(CLIENT);
		String input;
		do {
			input = ((String) JOptionPane.showInputDialog(null, "Host:", "Connect to chat server", JOptionPane.QUESTION_MESSAGE, null, null, SERVER_IP)).trim();
		} while (input == null || input.length() == 0);

		final String host = input;

		do {
			input = ((String) JOptionPane.showInputDialog(null, "Name:", "Connect to chat server", JOptionPane.QUESTION_MESSAGE, null, null, "Enter Name")).trim();
		} while (input == null || input.length() == 0);
		sessionID = input;

		new Thread("Connect") {
			public void run() {
				try {
					CLIENT.connect(5000, host, port);
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}.start();
	}

	static public class RegisterName {
		public String name;
	}

	static public class UpdateNames {
		public String[] names;
	}

	static public class ChatMessage {
		public String text;
	}
}