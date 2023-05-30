import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket {
    private final Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    public final String name;

    public ClientSocket(Socket socket, String clientName) {
        this.socket = socket;
        this.name = clientName;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void send(String text) {
        try {
            writer.printf("<%s> %s\n", name, text);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void receive() {
        Thread receiveThread =
                new Thread(
                        () -> {
                            try {
                                while (!socket.isClosed()) {
                                    String text = reader.readLine();
                                    if (text == null) {
                                        break;
                                    }
                                    System.out.println(text);
                                }
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            }
                        });
        receiveThread.start();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void close() {
        try {
            writer.close();
            reader.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void printInfo() {
        System.out.println("Client connected to: " + socket.getInetAddress().getHostName());
    }
}
