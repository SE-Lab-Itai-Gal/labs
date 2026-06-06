package il.cshaifasweng.tictactoe.entities;

import java.io.Serializable;

/**
 * A single immutable-ish message that travels (as a serialized object) between
 * the OCSF server and the OCSF clients in both directions. The meaning of the
 * fields depends on {@link #getType()}.
 */
public class GameMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final MessageType type;
    /** The symbol involved: 'X' or 'O' (0 when not relevant). */
    private final char symbol;
    /** The board cell index 0..8 (-1 when not relevant). */
    private final int cell;
    /** Whether it is the receiving player's turn after this message. */
    private final boolean yourTurn;
    /** Free text, e.g. the result of the game or the reason a move was rejected. */
    private final String text;

    public GameMessage(MessageType type, char symbol, int cell, boolean yourTurn, String text) {
        this.type = type;
        this.symbol = symbol;
        this.cell = cell;
        this.yourTurn = yourTurn;
        this.text = text;
    }

    /** Convenience for messages that only carry a type and a text. */
    public GameMessage(MessageType type, String text) {
        this(type, (char) 0, -1, false, text);
    }

    /** Convenience for a move request sent by a client (only the cell matters). */
    public static GameMessage move(int cell) {
        return new GameMessage(MessageType.MOVE, (char) 0, cell, false, null);
    }

    public MessageType getType() {
        return type;
    }

    public char getSymbol() {
        return symbol;
    }

    public int getCell() {
        return cell;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "GameMessage{" + type + ", symbol=" + symbol + ", cell=" + cell
                + ", yourTurn=" + yourTurn + ", text='" + text + "'}";
    }
}
