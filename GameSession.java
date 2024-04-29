import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameSession extends Thread {
    private Socket player1;
    private Socket player2;
    private List<List<Character>> board;
    private PrintWriter outputToPlayer1;
    private PrintWriter outputToPlayer2;
    private BufferedReader inputFromPlayer1;
    private BufferedReader inputFromPlayer2;

    public GameSession(Socket p1, Socket p2) {
        player1 = p1;
        player2 = p2;
        initializeBoard();
    }

    private void initializeBoard() {
        board = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<Character> row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                row.add(' ');
            }
            board.add(row);
        }
    }

    private void printBoardToPlayers() {
        StringBuilder boardString = new StringBuilder("\n");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardString.append(" ").append(board.get(i).get(j)).append(" ");
                if (j < 2) boardString.append("|");
            }
            boardString.append("\n");
            if (i < 2) boardString.append("---+---+---\n");
        }
        outputToPlayer1.println(boardString);
        outputToPlayer2.println(boardString);
    }

    public void run() {
        try {
            inputFromPlayer1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            inputFromPlayer2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            outputToPlayer1 = new PrintWriter(player1.getOutputStream(), true);
            outputToPlayer2 = new PrintWriter(player2.getOutputStream(), true);

            System.out.println("Game session started. IO streams created for both players.");

            outputToPlayer1.println("Player 1 connected, you are X");
            outputToPlayer2.println("Player 2 connected, you are O");
            printBoardToPlayers();

            boolean player1Turn = true;
            while (true) {
                if (player1Turn) {
                    outputToPlayer1.println("Your move");
                    processPlayerMove(player1, outputToPlayer1, inputFromPlayer1, 'X');
                } else {
                    outputToPlayer2.println("Your move");
                    processPlayerMove(player2, outputToPlayer2, inputFromPlayer2, 'O');
                }
                player1Turn = !player1Turn;
                if (isGameOver()) break;
            }
        } catch (IOException e) {
            System.out.println("Error in game session: " + e.getMessage());
        } finally {
            closeSockets();
            System.out.println("Game session ended.");
        }
    }

    private void processPlayerMove(Socket player, PrintWriter outputToPlayer, BufferedReader inputFromPlayer, char token) throws IOException {
        String move = inputFromPlayer.readLine();
        if (move != null) {
            int row = Integer.parseInt(move.split(" ")[0]);
            int col = Integer.parseInt(move.split(" ")[1]);
            if (isValidMove(row, col)) {
                board.get(row).set(col, token);
                outputToPlayer.println("Valid move.");
                printBoardToPlayers();
                broadcastMove(player, move);
            } else {
                outputToPlayer.println("Invalid move, try again.");
            }
        } else {
            outputToPlayer.println("No move received. Please make a move.");
        }
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && board.get(row).get(col) == ' ';
    }

    private void broadcastMove(Socket currentPlayer, String move) throws IOException {
        if (currentPlayer == player1) {
            outputToPlayer2.println("Opponent moved: " + move);
        } else {
            outputToPlayer1.println("Opponent moved: " + move);
        }
    }

    private boolean isGameOver() {
        // check rows and columns for a win
        for (int i = 0; i < 3; i++) {
            if (board.get(i).get(0) != ' ' && board.get(i).get(0) == board.get(i).get(1) && board.get(i).get(1) == board.get(i).get(2)) {
                return true; 
            }
            if (board.get(0).get(i) != ' ' && board.get(0).get(i) == board.get(1).get(i) && board.get(1).get(i) == board.get(2).get(i)) {
                return true; 
            }
        }
        System.out.println("isGameOver error");
        return false;
    }

    private void closeSockets() {
        try {
            if (player1 != null) player1.close();
            if (player2 != null) player2.close();
        } catch (IOException e) {
            System.out.println("Error closing sockets: " + e.getMessage());
        }
    }
}
