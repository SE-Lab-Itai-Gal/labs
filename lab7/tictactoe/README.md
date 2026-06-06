# Lab 7 — Multiplayer Tic-Tac-Toe (OCSF + EventBus)

A two-player networked Tic-Tac-Toe game built on the **OCSF** (Object Client-Server
Framework) client-server architecture, a **JavaFX** GUI client, and the **EventBus**
(GreenRobot) Publish/Subscribe pattern.

Two players connect to the server, are randomly assigned `X`/`O` and a starting turn,
and play against each other. Every board update flows through the server so the two
GUIs always stay in sync.

## Project structure

A Maven multi-module project, mirroring the OCSF mediator example layout:

| Module | Contents |
| --- | --- |
| `entities` | Shared, serializable protocol: `GameMessage` + `MessageType`. |
| `server`   | OCSF server (`server/ocsf/*`), `GameServer` (game logic), `App` (entry point). |
| `client`   | JavaFX app (`App`, `Main`, `board.fxml`), OCSF client (`client/ocsf/*`), `GameClient`, `BoardController`, `GameEvent`. |

### How the pieces fit together

```
 [JavaFX BoardController]                         [GameServer (OCSF)]
        |  click -> sendToServer(MOVE)                  |
        v                                               |
   [GameClient : AbstractClient]  --- TCP/socket --->   | handleMessageFromClient
        ^                                               |  - validates turn/cell
        |  handleMessageFromServer                      |  - updates the board
        |  posts GameEvent on EventBus                  |  - checks win/draw
        |                                               v
 [EventBus]  -- onGameEvent -->  [BoardController updates the grid]
```

* The **server is the single source of truth** for the board. A client never draws a
  move on its own — it asks the server, and the server echoes the validated move back
  to *both* players. This guarantees the two boards never diverge.
* On the client, networking and UI are **decoupled with EventBus**: `GameClient`
  republishes every server message as a `GameEvent`; the `BoardController` subscribes
  (`@Subscribe`) and updates the JavaFX scene on the FX thread (`Platform.runLater`).

### Message protocol (`GameMessage` / `MessageType`)

| Direction | Message | Meaning |
| --- | --- | --- |
| C → S | `"join"` | I want to play. |
| C → S | `MOVE(cell)` | Place my symbol on `cell` (0–8). |
| C → S | `"quit"` | Leaving (sent on window close). |
| S → C | `WAITING_FOR_OPPONENT` | Connected, waiting for a second player. |
| S → C | `GAME_START(symbol, yourTurn)` | Game started; you are `X`/`O`; is it your turn. |
| S → C | `MOVE(symbol, cell, yourTurn)` | A validated move (echoed to both players). |
| S → C | `INVALID_MOVE(text)` | Move rejected (not your turn / cell taken). |
| S → C | `GAME_OVER(text)` | `You win!` / `You lose!` / `It's a draw!` |
| S → C | `OPPONENT_DISCONNECTED` | The other player left. |
| S → C | `GAME_FULL` | A game is already in progress. |

`X`/`O` assignment and who plays first are chosen **randomly** by the server on each
new game.

## Building

JavaFX 21 requires JDK 17+. Build with a Java 17/21 JDK:

```bash
cd lab7/tictactoe
mvn clean install        # uses the JDK on JAVA_HOME (must be 17+)
```

This produces two runnable jars (also copied to `dist/`):

* `server/target/tictactoe-server.jar`
* `client/target/tictactoe-client.jar` — a fat jar that bundles JavaFX **and the
  native libraries for Linux, Windows and macOS**, so the same jar runs on all three.

## Running

### On a single computer

```bash
# 1) start the server (default port 3000; pass another port as an argument)
java -jar dist/tictactoe-server.jar 3000

# 2) start two clients (two windows)
java -jar dist/tictactoe-client.jar localhost 3000
java -jar dist/tictactoe-client.jar localhost 3000
```

### On two different computers

1. Run the server on one machine and note its IP address (e.g. `192.168.1.20`).
2. Make sure both machines are on the same network and the server port is reachable
   (open the port in the firewall if needed).
3. On each computer run a client pointing at the server's IP:

   ```bash
   java -jar tictactoe-client.jar 192.168.1.20 3000
   ```

> The server listens on port `3000` by default; you may pass any port `> 1024` as the
> first argument to the server and as the second argument to each client.
> See the **"solution to the communication problem between two computers"** document
> from Lab 6 for network/firewall setup between two hosts.

## Running from the IDE (IntelliJ / Maven)

* Server: Maven goal `clean install exec:java` with working directory `server`.
* Client: Maven goal `javafx:run` with working directory `client`
  (use *Run → Modify options → Allow multiple instances* to launch two clients).
