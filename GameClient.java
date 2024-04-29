import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;

    public GameClient(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        scanner = new Scanner(System.in);
        System.out.println("Connected to the server at " + serverAddress + ":" + serverPort);
    }

    public void start() {
        try {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server says: " + fromServer);
                if ("Your move".equals(fromServer)) {
                    System.out.print("Enter your move (row col): ");
                    String userMove = scanner.nextLine();
                    out.println(userMove);
                }
            }
        } catch (IOException e) {
            System.out.println("Error communicating with the server: " + e.getMessage());
        } finally {
            closeEverything();
        }
    }

    private void closeEverything() {
        try {
            if (scanner != null) scanner.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Scanner IPinput = new Scanner(System.in);
            String IP = new String();
            System.out.print("enter IP (localhost if local): ");
            IP = IPinput.nextLine();

            GameClient client = new GameClient(IP, 8000);
            client.start();
        } catch (IOException e) {
            System.out.println("Unable to connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
