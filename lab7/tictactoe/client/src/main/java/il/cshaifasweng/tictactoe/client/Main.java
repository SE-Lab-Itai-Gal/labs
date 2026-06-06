package il.cshaifasweng.tictactoe.client;

/**
 * Plain launcher used by the shaded ("fat") jar.
 *
 * <p>A jar whose {@code Main-Class} extends {@link javafx.application.Application}
 * fails to start with "JavaFX runtime components are missing". Delegating from a
 * non-Application class avoids that, so the produced jar runs with a simple
 * {@code java -jar tictactoe-client.jar [host] [port]}.</p>
 */
public class Main {

    public static void main(String[] args) {
        App.main(args);
    }
}
