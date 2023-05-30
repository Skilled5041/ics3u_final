import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

public class ServerThread extends Thread {
    private final Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {

            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String text;
            do {
                text = reader.readLine();
                sendToAllConnectedClients(text);
            } while (text != null && !text.equalsIgnoreCase("end") && !socket.isClosed());

            reader.close();
            writer.close();
            socket.close();

            ServerClient.serverThreads.remove(this);
            System.out.println("Client disconnected.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void send(String text) {
        try {
            writer.println(text);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendToAllConnectedClients(String text) {
        for (ServerThread serverThread : ServerClient.serverThreads) {
            serverThread.send(text);
        }
    }
}
