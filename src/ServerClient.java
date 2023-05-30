import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerClient {
    public static List<ServerThread> serverThreads;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(7272);
            System.out.println("Server has started on port " + server.getLocalPort() + ".");

            serverThreads = new ArrayList<>();

            while (!server.isClosed()) {
                Socket socket = server.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                ServerThread serverThread = new ServerThread(socket);
                serverThreads.add(serverThread);
                serverThread.start();
            }

            server.close();
            System.out.println("Server has stopped.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
