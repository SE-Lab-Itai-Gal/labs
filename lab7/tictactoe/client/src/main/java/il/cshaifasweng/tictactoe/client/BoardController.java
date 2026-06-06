package il.cshaifasweng.tictactoe.client;

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import il.cshaifasweng.tictactoe.entities.GameMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the 3x3 board. It is the EventBus subscriber: it reacts to
 * {@link GameEvent}s posted by {@link GameClient} and translates user clicks
 * into MOVE requests sent to the server. The board is only ever updated from
 * server messages, so both players always see the same state.
 */
public class BoardController {

    @FXML private Label statusLabel;
    @FXML private Button b0, b1, b2, b3, b4, b5, b6, b7, b8; // The 9 ticktac toe cell buttons

    private Button[] cells;
    private char mySymbol = 0;
    private boolean myTurn = false;
    private boolean gameOver = true;

    @FXML
    private void initialize() {
        cells = new Button[]{b0, b1, b2, b3, b4, b5, b6, b7, b8};
        EventBus.getDefault().register(this);
        statusLabel.setText("Connecting...");
        refreshButtons();
        try {
            GameClient.getClient().sendToServer("join"); // Request to join the game
        } catch (IOException ex) {
            statusLabel.setText("Could not reach the server.");
            ex.printStackTrace();
        }
    }

    /** Called by every board button (they all share this handler). */
    @FXML
    private void handleClick(ActionEvent event) {
        if (gameOver || !myTurn) {
            return;
        }
        int index = indexOf((Button) event.getSource());
        if (index < 0 || !cells[index].getText().isEmpty()) {
            return;
        }
        try {
            // Do not draw locally; wait for the server to echo the move back.
            GameClient.getClient().sendToServer(GameMessage.move(index));
        } catch (IOException ex) {
            statusLabel.setText("Failed to send move.");
            ex.printStackTrace();
        }
    }

    @Subscribe // A game event subscriber,  Called when a game message arrives from the server
    public void onGameEvent(GameEvent event) {
        GameMessage msg = event.getMessage();
        Platform.runLater(() -> handleMessage(msg)); // UI update code thus needs to run in Platform.runLater so its synchronized in the UI thread
    }

    /**
     * 
     * @param msg - A message object with information about the game state
     */
    private void handleMessage(GameMessage msg) {
        switch (msg.getType()) {
            case WAITING_FOR_OPPONENT:
                gameOver = true;
                myTurn = false;
                statusLabel.setText(msg.getText());
                break;

            case GAME_START:
                mySymbol = msg.getSymbol();
                gameOver = false;
                myTurn = msg.isYourTurn();
                clearBoard();
                statusLabel.setText("You are '" + mySymbol + "'. "
                        + (myTurn ? "Your turn." : "Opponent's turn."));
                break;

            case MOVE: // An actual move, Changes both the turn & A cell
                if (msg.getCell() >= 0) {
                    cells[msg.getCell()].setText(String.valueOf(msg.getSymbol()));
                }
                myTurn = msg.isYourTurn();
                if (!gameOver) {
                    statusLabel.setText(myTurn ? "Your turn." : "Opponent's turn.");
                }
                break;

            case INVALID_MOVE: // A previous move was invalid, Tell the player what is the reason for the fail
                statusLabel.setText(msg.getText());
                break;

            case GAME_OVER:
                if (msg.getCell() >= 0 && msg.getSymbol() != 0) {
                    cells[msg.getCell()].setText(String.valueOf(msg.getSymbol()));
                }
                gameOver = true;
                myTurn = false; // Make the game read only
                statusLabel.setText("Game over - " + msg.getText()); // The text says who won 
                break;

            case OPPONENT_DISCONNECTED: // The oppnent diconnected, Stop the game
                gameOver = true;
                myTurn = false;
                clearBoard();
                statusLabel.setText(msg.getText());
                break;

            case GAME_FULL: // The game is full, end the game, read only
                gameOver = true;
                myTurn = false;
                statusLabel.setText(msg.getText());
                break;

            default:
                break;
        }
        refreshButtons(); // Refresh the buttons so they update to the new state
    }

    private void clearBoard() {
        for (Button cell : cells) {
            cell.setText("");
        }
    }

    /** Empty cells are clickable only on the player's turn while the game runs. */
    private void refreshButtons() {
        boolean canPlay = myTurn && !gameOver;
        for (Button cell : cells) {
            boolean empty = cell.getText().isEmpty();
            cell.setDisable(!(canPlay && empty));
        }
    }

    private int indexOf(Button button) {
        for (int i = 0; i < cells.length; i++) {
            if (cells[i] == button) {
                return i;
            }
        }
        return -1;
    }
}
