import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;
    private Set<ClientHandler> clientHandlers;

    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your nickname:");
            nickname = in.readLine();

            // 🔒 Ensure unique nickname
            synchronized (clientHandlers) {
                boolean taken = true;
                String original = nickname;
                int count = 1;

                while (taken) {
                    taken = false;
                    for (ClientHandler client : clientHandlers) {
                        if (client.nickname != null && client.nickname.equalsIgnoreCase(nickname)) {
                            // nickname already taken → try new one
                            nickname = original + count;
                            count++;
                            taken = true;
                            break;
                        }
                    }
                }
                if (!nickname.equals(original)) {
                    out.println("⚠️ Nickname taken, you are now " + nickname);
                }
            }

            broadcast("🟢 " + nickname + " joined the chat!");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
                broadcast(nickname + ": " + message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientHandlers.remove(this);
            broadcast("🔴 " + nickname + " left the chat.");
        }
    }

    private void broadcast(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                client.out.println(message);
            }
        }
    }
}
