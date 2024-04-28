import java.net.ServerSocket;
import java.net.Socket;

public class TicTacToeServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            System.out.println("Server ready for connections...");
            while (true) {
                Socket player1 = serverSocket.accept();
                System.out.println("Player 1 connected");
                Socket player2 = serverSocket.accept();
                System.out.println("Player 2 connected");

                GameSession game = new GameSession(player1, player2);
                game.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
