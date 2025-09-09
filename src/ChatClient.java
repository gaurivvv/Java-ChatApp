import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private BufferedReader userInput;

    public ChatClient(String serverAddress, int serverPort) {
        try {
            Socket socket = new Socket("localhost", 12345);

            System.out.println("✅ Connected to chat server");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            // Thread to read messages from server
            new Thread(new ServerListener()).start();

            // Thread to send messages to server
            while (true) {
                String msg = userInput.readLine();
                if (msg.equalsIgnoreCase("exit")) {
                    out.println("left the chat.");
                    socket.close();
                    break;
                }
                out.println(msg);
            }

        } catch (IOException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("⚠️ Disconnected from server.");
            }
        }
    }

    public static void main(String[] args) {
        new ChatClient("localhost", 1234); // Connect to server running locally
    }
}
