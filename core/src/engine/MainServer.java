package engine;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import engine.Net.ChatMessage;
import engine.Net.RegisterName;
import engine.Net.UpdateNames;

import java.io.IOException;
import java.util.ArrayList;

public class MainServer {
	Server server;

	public MainServer() throws IOException {
		server = new Server() {
			protected Connection newConnection() {
				return new ChatConnection();
			}
		};

		Net.register(server);

		server.addListener(new Listener() {
			public void received(Connection c, Object object) {
				ChatConnection connection = (ChatConnection) c;

				if (object instanceof RegisterName) {
					if (connection.name != null)
						return;
					String name = ((RegisterName) object).name;
					if (name == null)
						return;
					name = name.trim();
					if (name.length() == 0)
						return;
					connection.name = name;
					ChatMessage chatMessage = new ChatMessage();
					chatMessage.text = name + " connected.";
					server.sendToAllExceptTCP(connection.getID(), chatMessage);
					updateNames();
					return;
				}

				if (object instanceof ChatMessage) {
					if (connection.name == null)
						return;
					ChatMessage chatMessage = (ChatMessage) object;
					String message = chatMessage.text;
					if (message == null)
						return;
					message = message.trim();
					if (message.length() == 0)
						return;
					chatMessage.text = connection.name + ": " + message;
					server.sendToAllTCP(chatMessage);
					return;
				}
			}

			public void disconnected(Connection c) {
				ChatConnection connection = (ChatConnection) c;
				if (connection.name != null) {
					ChatMessage chatMessage = new ChatMessage();
					chatMessage.text = connection.name + " disconnected.";
					server.sendToAllTCP(chatMessage);
					updateNames();
				}
			}
		});
		server.bind(Net.port);
		server.start();
	}

	public static void main(String[] args) throws IOException {
		Log.set(Log.LEVEL_DEBUG);
		new MainServer();
	}

	void updateNames() {
		Connection[] connections = server.getConnections();
		ArrayList<String> names = new ArrayList<String>(connections.length);
		for (int i = connections.length - 1; i >= 0; i--) {
			ChatConnection connection = (ChatConnection) connections[i];
			names.add(connection.name);
		}
		UpdateNames updateNames = new UpdateNames();
		updateNames.names = names.toArray(new String[names.size()]);
		server.sendToAllTCP(updateNames);
	}

	static class ChatConnection extends Connection {
		public String name;
	}
}