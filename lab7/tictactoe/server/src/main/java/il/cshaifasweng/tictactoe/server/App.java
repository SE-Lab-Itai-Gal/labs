package il.cshaifasweng.tictactoe.server;

import java.io.IOException;

/**
 * Entry point of the Tic-Tac-Toe server.
 *
 * <p>Usage: {@code java -jar tictactoe-server.jar [port]} (default port 3000).</p>
 */
public class App {

    private static final int DEFAULT_PORT = 3000;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.err.println("Invalid port '" + args[0] + "', falling back to " + DEFAULT_PORT);
            }
        }

        GameServer server = new GameServer(port); // Start the server
        server.listen();
        System.out.println("Tic-Tac-Toe server is listening on port " + port);
    }
}
