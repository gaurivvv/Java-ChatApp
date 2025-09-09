import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<String> clientNames = new HashSet<>();
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Chat server started...");
        ServerSocket server = new ServerSocket(12345);

        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String name;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // ✅ Ask for nickname
                while (true) {
                    out.println("Enter your nickname:");
                    name = in.readLine();

                    if (name == null) {
                        return;
                    }

                    synchronized (clientNames) {
                        if (!clientNames.contains(name)) {
                            clientNames.add(name);
                            break;
                        } else {
                            out.println("❌ Nickname already taken, try another one!");
                        }
                    }
                }

                out.println("✅ Welcome " + name + "!");
                System.out.println(name + " joined the chat");

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // Broadcast join message
                for (PrintWriter writer : clientWriters) {
                    writer.println("? " + name + " joined the chat!");
                }

                // Listen for messages
                String message;
                while ((message = in.readLine()) != null) {
                    for (PrintWriter writer : clientWriters) {
                        writer.println(name + ": " + message);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                // Cleanup
                if (name != null) {
                    clientNames.remove(name);
                    for (PrintWriter writer : clientWriters) {
                        writer.println("? " + name + " left the chat.");
                    }
                }
                if (out != null) {
                    clientWriters.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
