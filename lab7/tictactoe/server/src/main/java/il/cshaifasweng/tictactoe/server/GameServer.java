package il.cshaifasweng.tictactoe.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import il.cshaifasweng.tictactoe.entities.GameMessage;
import il.cshaifasweng.tictactoe.entities.MessageType;
import il.cshaifasweng.tictactoe.server.ocsf.AbstractServer;
import il.cshaifasweng.tictactoe.server.ocsf.ConnectionToClient;

/**
 * The OCSF server that hosts a single two-player Tic-Tac-Toe game.
 *
 * <p>The server is the single source of truth for the board: clients never
 * update their board on their own, they only ask the server to place a symbol
 * and the server echoes the validated move back to both players. This keeps the
 * two GUIs perfectly in sync.</p>
 */
public class GameServer extends AbstractServer {

    /** Key used to remember which symbol ('X'/'O') a connection plays. */
    private static final String SYMBOL_KEY = "symbol";

    private final Random random = new Random();

    /** The (at most two) players currently in the game. */
    private final List<ConnectionToClient> players = new ArrayList<>();

    /** The 3x3 board, indices 0..8, ' ' means empty. */
    private final char[] board = new char[9];

    /** Whose turn it is: 'X' or 'O'. */
    private char currentTurn;

    /** True while a game is running (two players, no winner yet). */
    private boolean gameActive = false;

    public GameServer(int port) {
        super(port);
        clearBoard();
    }

    @Override
    protected synchronized void handleMessageFromClient(Object msg, ConnectionToClient client) {
        try {
            if (msg instanceof String) {
                String command = (String) msg;
                if ("join".equals(command)) {
                    handleJoin(client);
                } else if ("quit".equals(command)) {
                    removePlayer(client);
                }
            } else if (msg instanceof GameMessage) {
                GameMessage gm = (GameMessage) msg;
                if (gm.getType() == MessageType.MOVE) {
                    handleMove(client, gm.getCell());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** A client asked to join the game. */
    private void handleJoin(ConnectionToClient client) throws IOException {
        if (players.contains(client)) {
            return;
        }
        if (players.size() >= 2) { // We only support one game at a time, If the game is full return an error
            client.sendToClient(new GameMessage(MessageType.GAME_FULL,
                    "A game is already in progress. Please try again later."));
            return;
        }
        players.add(client); // Add that client to the list of players
        System.out.println("Player joined from " + client.getInetAddress()
                + " (" + players.size() + "/2)");
        if (players.size() == 1) {
            client.sendToClient(new GameMessage(MessageType.WAITING_FOR_OPPONENT,
                    "Waiting for an opponent to connect...")); // If only 1 client, Return that the client needs to wait for another player in order for the game to start
        } else {
            startGame(); // If there are two clients we can start the game :)
        }
    }

    /** Starts a fresh game, randomly assigning symbols and the first turn. */
    private void startGame() throws IOException {
        clearBoard();

        // Randomly decide which connected player gets 'X'.
        ConnectionToClient first = players.get(0);
        ConnectionToClient second = players.get(1);
        if (random.nextBoolean()) { // Decide who starts randomly via a random boolean
            ConnectionToClient tmp = first;
            first = second;
            second = tmp;
        }
        first.setInfo(SYMBOL_KEY, 'X');
        second.setInfo(SYMBOL_KEY, 'O');

        // Randomly decide who starts.
        currentTurn = random.nextBoolean() ? 'X' : 'O';
        gameActive = true;
        System.out.println("Game started. " + currentTurn + " plays first.");

        for (ConnectionToClient player : players) {
            char symbol = (char) player.getInfo(SYMBOL_KEY);
            boolean yourTurn = (symbol == currentTurn);
            player.sendToClient(new GameMessage(MessageType.GAME_START, symbol, -1, yourTurn,
                    "Game started! You are '" + symbol + "'."));
        }// Tell each client what symbol they will be
    }

    /** Handles a move request from a player. */
    private void handleMove(ConnectionToClient client, int cell) throws IOException {
        if (!gameActive) {
            return;
        }
        Object info = client.getInfo(SYMBOL_KEY);
        if (info == null) {
            return; // not a player of this game
        }
        char symbol = (char) info;

        if (symbol != currentTurn) { // Out of turn move
            client.sendToClient(new GameMessage(MessageType.INVALID_MOVE, "It is not your turn."));
            return;
        }
        if (cell < 0 || cell > 8 || board[cell] != ' ') { // Illegal cell out of the board
            client.sendToClient(new GameMessage(MessageType.INVALID_MOVE, "That cell is not available."));
            return;
        }

        board[cell] = symbol;

        char winner = checkWinner();
        boolean boardFull = isBoardFull();

        if (winner != ' ' || boardFull) { // Gameover
            // Echo the final move (no one's turn afterwards), then announce the result.
            broadcastMove(symbol, cell, ' ');
            gameActive = false;
            for (ConnectionToClient player : players) {
                char playerSymbol = (char) player.getInfo(SYMBOL_KEY);
                String result;
                if (winner == ' ') { // The board is full & no winner -> DRAW
                    result = "It's a draw!";
                } else {
                    result = (winner == playerSymbol) ? "You win!" : "You lose!";
                }
                player.sendToClient(new GameMessage(MessageType.GAME_OVER, playerSymbol, cell, false, result));
            }
            System.out.println("Game over. " + (winner == ' ' ? "Draw." : winner + " wins."));
        } else { // Normal move
            currentTurn = (symbol == 'X') ? 'O' : 'X';
            broadcastMove(symbol, cell, currentTurn);
        }
    }

    /** Sends a MOVE update to both players, telling each whether it is now its turn. */
    private void broadcastMove(char symbol, int cell, char nextTurn) throws IOException {
        for (ConnectionToClient player : players) {
            char playerSymbol = (char) player.getInfo(SYMBOL_KEY);
            boolean yourTurn = (nextTurn != ' ') && (playerSymbol == nextTurn);
            player.sendToClient(new GameMessage(MessageType.MOVE, symbol, cell, yourTurn, null));
        }
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        removePlayer(client); // If a client disconnected
    }

    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        removePlayer(client);
    }

    /** Removes a player and notifies the one left behind that the game has ended. */
    private void removePlayer(ConnectionToClient client) { // If a player disconnected we reomve it, Restart the game for the other opponent
        if (!players.remove(client)) {
            return;
        }
        System.out.println("A player disconnected. Remaining: " + players.size());
        gameActive = false;
        clearBoard();
        for (ConnectionToClient player : players) {
            player.setInfo(SYMBOL_KEY, null);
            try {
                player.sendToClient(new GameMessage(MessageType.OPPONENT_DISCONNECTED,
                        "Your opponent left. Waiting for a new opponent..."));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void clearBoard() {
        for (int i = 0; i < board.length; i++) {
            board[i] = ' ';
        }
    }

    private boolean isBoardFull() { // Is the board full?
        for (char c : board) {
            if (c == ' ') {
                return false;
            }
        }
        return true;
    }

    /** Returns the winning symbol ('X'/'O'), or ' ' if there is no winner yet. */
    private char checkWinner() { // Check if there is a winning pattern out of all the possible win patterns if yes return true.
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
                {0, 4, 8}, {2, 4, 6}             // diagonals
        };
        for (int[] line : lines) {
            char a = board[line[0]];
            if (a != ' ' && a == board[line[1]] && a == board[line[2]]) {
                return a;
            }
        }
        return ' ';
    }
}
