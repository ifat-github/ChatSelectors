package il.co.ilrd.chatselectors;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChatServerSelector {

	private Selector selector;
	private ServerSocketChannel socket;
	private int port;
	private InetSocketAddress address;
	private List<SocketChannel> clients;
	private boolean run;

	public ChatServerSelector(int port) {
		this.port = port;
		this.run = true;
		clients = new ArrayList<>();
		try {
			selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			socket = ServerSocketChannel.open();
			address = new InetSocketAddress("10.1.0.178", port);
			socket.bind(address);
			socket.configureBlocking(false);
			int ops = socket.validOps();
			socket.register(selector, ops, null);

			while (run) {
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> keysIterator = keys.iterator();

				while (keysIterator.hasNext()) {
					SelectionKey myKey = keysIterator.next();
					SocketChannel client;

					if (myKey.isAcceptable()) {
						client = socket.accept();
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_READ);
						clients.add(client);

					} else if (myKey.isReadable()) {
						client = (SocketChannel) myKey.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						client.read(buffer);
						String result = new String(buffer.array());

						if (result.equals("exit")) {
							clients.remove(client);
						}

						byte[] byteMessage = result.getBytes();
						buffer = ByteBuffer.wrap(byteMessage);

						for (int i = 0; i < clients.size(); i++) {
							clients.get(i).write(buffer);
							buffer.rewind();
						}
					}
					keysIterator.remove();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		ChatServerSelector server = new ChatServerSelector(8900);
		server.run();
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}