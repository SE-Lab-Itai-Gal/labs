package il.cshaifasweng.tictactoe.entities;

/**
 * The kinds of messages exchanged between the Tic-Tac-Toe server and clients.
 */
public enum MessageType {
    /** Server -> client: you are connected but waiting for a second player. */
    WAITING_FOR_OPPONENT,
    /** Server -> client: a game just started. Carries the player's symbol and whether it is his turn. */
    GAME_START,
    /** client -> server: this player wants to place his symbol on a cell.
     *  Server -> client: a cell was filled (echoed to both players). */
    MOVE,
    /** Server -> client: the requested move was rejected (wrong turn / cell taken). */
    INVALID_MOVE,
    /** Server -> client: the game is over. The text holds the result for this player. */
    GAME_OVER,
    /** Server -> client: the opponent left the game. */
    OPPONENT_DISCONNECTED,
    /** Server -> client: a game is already in progress, no room for another player. */
    GAME_FULL
}
