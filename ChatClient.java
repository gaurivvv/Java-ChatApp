import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_IP = "127.0.0.1"; // localhost
    private static final int PORT = 1234;

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(SERVER_IP, PORT);
        System.out.println("Connected to chat server");

        // Thread to read messages from server
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Sending messages to server
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        String text;
        while ((text = console.readLine()) != null) {
            out.println(text);
        }
    }
}
