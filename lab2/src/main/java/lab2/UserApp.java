package lab2;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class UserApp extends Application {
    private static String FILE_PATH = "Users.txt";
    private static ArrayList<User> users = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
            // Load the FXML file from the resources folder
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab2/Login.fxml"));
        Parent root = loader.load();

        // Create the Login scene and show the window
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            BufferedReader r = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            while((line = r.readLine()) != null) {
                try {
                    User u = new User(line);
                    users.add(u);
                    System.out.println("Created a new user: " + u.getUser());
                } catch (User.InvalidCredsException e) {
                    System.err.println(e.getMessage());
                }
            }
            r.close();

        } catch(IOException e){
            System.err.println("Got exception: " + e);
        }


        Collections.sort(users, (a, b) -> a.getUser().compareTo(b.getUser()));
        for(User user:users){
            System.out.println(user);
        }

        launch(args);
    }
}
