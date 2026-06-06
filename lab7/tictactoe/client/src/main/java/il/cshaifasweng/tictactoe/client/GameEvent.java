package il.cshaifasweng.tictactoe.client;

import il.cshaifasweng.tictactoe.entities.GameMessage;

/**
 * EventBus event that wraps a {@link GameMessage} received from the server.
 * The OCSF client posts it on the bus and the JavaFX controller (the
 * subscriber) reacts to it. This decouples the networking code from the UI,
 * following the Publish/Subscribe (mediator) pattern.
 */
public class GameEvent {

    private final GameMessage message;

    public GameEvent(GameMessage message) {
        this.message = message;
    }

    public GameMessage getMessage() {
        return message;
    }
}
