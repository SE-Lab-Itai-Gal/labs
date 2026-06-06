package il.cshaifasweng.tictactoe.client;

import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX application: opens the OCSF connection and shows the game board.
 *
 * Its the client which connects by default to localhost:3000
 */
public class App extends Application {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3000;

    private GameClient client;

    @Override
    public void start(Stage stage) throws IOException {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;


        // run with: ./<exec_name> <host> <port>
        List<String> args = getParameters().getRaw();
        if (args.size() >= 1) { // If not set keeps the default executable
            host = args.get(0);
        }
        if (args.size() >= 2) {
            try {
                port = Integer.parseInt(args.get(1));
            } catch (NumberFormatException ignored) {
                // keep default port
            }
        }

        client = GameClient.getClient(host, port);
        client.openConnection();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("board.fxml"));
        Parent root = loader.load();

        stage.setTitle("Tic-Tac-Toe  (" + host + ":" + port + ")");
        stage.setScene(new Scene(root, 360, 440));
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (client != null && client.isConnected()) {
            try {
                client.sendToServer("quit");
            } catch (IOException ignored) {
                // closing anyway
            }
            client.closeConnection();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
