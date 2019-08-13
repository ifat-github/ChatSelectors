package il.co.ilrd.chatselectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChatClientSelector {

	private String name;
	private SocketChannel socket;
	private BufferedReader consoleInput;

	public ChatClientSelector(String ip, int port, String name) {
		try {
			InetSocketAddress host = new InetSocketAddress(ip, port);
			socket = SocketChannel.open(host);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.name = name;
	}

	public void run() {
		Thread consoleInputThread = new Thread(this::recieveConsoleInput);
		Thread serverInputThread = new Thread(this::recieveServerInput);

		consoleInputThread.start();
		serverInputThread.start();
	}

	private void sendInput(String str) {
		String message = name + ":" + str;
		try {
			byte[] byteMessage = message.getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(byteMessage);
			socket.write(buffer);
			buffer.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void recieveConsoleInput() {
		while (socket.isConnected()) {
			consoleInput = new BufferedReader(new InputStreamReader(System.in));
			try {
				sendInput(consoleInput.readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void recieveServerInput() {
		while (socket.isConnected()) {
			ByteBuffer buf = ByteBuffer.allocate(1024);
			try {
				while (socket.read(buf) > 0) {
					System.out.println(new String(buf.array()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}