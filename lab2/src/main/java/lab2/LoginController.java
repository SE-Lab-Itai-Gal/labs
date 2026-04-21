package lab2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    void handleLogin(ActionEvent event) {
        String inputUser = usernameField.getText();
        String inputPass = passwordField.getText();

        boolean isMatch = false;

        // Loop over valid users to find a match
        for (User u : UserApp.getUsers()) {
            if (u.getUser().equals(inputUser) && u.getPass().equals(inputPass)) {
                isMatch = true;
                break;
            }
        }

        if (isMatch) {
            try {
                // 2.3: On successful login, load and show the Welcome screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab2/Welcome.fxml"));
                Parent root = loader.load();
                
                // Get the current window (stage)
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                
                // Switch scenes
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 2.3: If wrong, display an error message directly on the screen
            errorLabel.setText("username or password do not match");
            errorLabel.setTextFill(Color.RED);
        }
    }
}
