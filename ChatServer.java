import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 1234;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Server started on port " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket);
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("Received: " + msg);
                    broadcast(msg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }

        private void broadcast(String msg) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(msg);
                }
            }
        }
    }
}
