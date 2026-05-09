package lab3;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    @FXML
    void handleLogin(ActionEvent event) {
        String inputUser = usernameField.getText();
        String inputPass = passwordField.getText();

        // look for a user with this username + password
        User matched = null;
        for (User u : UserApp.getUsers()) {
            if (u.getUser().equals(inputUser) && u.getPass().equals(inputPass)) {
                matched = u;
                break;
            }
        }

        if (matched != null) {
            // 3b - open a thread that checks the user isn't blocked before letting them in
            final User user = matched;
            new Thread(() -> {
                if (user.succeed()) {
                    Platform.runLater(() -> switchToWelcome(event));
                } else {
                    // user is blocked, show how much time is left
                    long remaining = user.remainingBlockSeconds();
                    Platform.runLater(() -> {
                        errorLabel.setTextFill(Color.RED);
                        errorLabel.setText("User is blocked. Try again in " + remaining + "s");
                    });
                }
            }, "verify-thread").start();
        } else {
            // 3a - open a thread that adds 1 to the failed counter, and blocks if we hit n
            new Thread(() -> {
                User u = UserApp.findUser(inputUser);
                int attempts = (u != null) ? u.fail() : 0;
                // did this attempt cause the block?
                boolean justBlocked = (u != null) && attempts == UserApp.getMaxAttempts() && u.isBlocked();
                final int finalAttempts = attempts;
                final User foundUser = u;
                Platform.runLater(() -> {
                    if (justBlocked) {
                        startCooldown();
                    } else {
                        errorLabel.setTextFill(Color.RED);
                        errorLabel.setText("Wrong username or password" +
                                (foundUser != null ? " (attempt " + finalAttempts + "/" + UserApp.getMaxAttempts() + ")" : ""));
                    }
                });
            }, "fail-thread").start();
        }
    }

    private void switchToWelcome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab3/Welcome.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 5 - after n failures show a message, wait t seconds and then let the user try again
    private void startCooldown() {
        int t = UserApp.getBlockSeconds();
        setInputsDisabled(true);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setText("Reached " + UserApp.getMaxAttempts() + " failed attempts. Wait " + t + "s.");

        // count down every second so the user sees how much time is left
        new Thread(() -> {
            for (int i = t; i > 0; i--) {
                final int sec = i;
                Platform.runLater(() -> errorLabel.setText("Blocked. " + sec + "s remaining..."));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            // time is up, enable the inputs again
            Platform.runLater(() -> {
                errorLabel.setTextFill(Color.GREEN);
                errorLabel.setText("You may try again.");
                setInputsDisabled(false);
            });
        }, "cooldown-timer").start();
    }

    private void setInputsDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        passwordField.setDisable(disabled);
        if (loginButton != null) loginButton.setDisable(disabled);
    }
}
