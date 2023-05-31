import java.net.Socket;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    try {
      Scanner sc = new Scanner(System.in);
      ClientSocket client = new ClientSocket(new Socket("localhost", 7272), "example_username");

      client.printInfo();
      client.receive();

      while (!client.isClosed()) {
        String text = sc.nextLine();
        if (text.equalsIgnoreCase("stop")) {
          break;
        }
        client.send(text);
      }

      sc.close();
      client.close();

    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
