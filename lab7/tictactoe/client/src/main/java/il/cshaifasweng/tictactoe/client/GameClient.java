package il.cshaifasweng.tictactoe.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.tictactoe.client.ocsf.AbstractClient;
import il.cshaifasweng.tictactoe.entities.GameMessage;

/**
 * OCSF client for the Tic-Tac-Toe game. It is a singleton so that the JavaFX
 * {@code App} and the controller share the same connection. Every message that
 * arrives from the server is republished on the EventBus, so the UI never talks
 * to the socket directly.
 */
public class GameClient extends AbstractClient {

    private static GameClient client = null;

    private GameClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        if (msg instanceof GameMessage) {
            EventBus.getDefault().post(new GameEvent((GameMessage) msg));
        } else {
            System.out.println("Server: " + msg);
        }
    }

    /** Returns the shared client, creating it on first use. */
    public static GameClient getClient(String host, int port) {
        if (client == null) {
            client = new GameClient(host, port);
        }
        return client;
    }

    /** Returns the already-created shared client. */
    public static GameClient getClient() {
        return client;
    }
}
