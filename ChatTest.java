package il.co.ilrd.chatselectors;

public class ChatTest {
	public static void main(String[] args) {
		ChatClientSelector client1 = new ChatClientSelector("10.1.0.199", 50000, "ifat");
		client1.run();
	}
}
