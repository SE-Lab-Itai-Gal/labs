package lab3;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class UserApp extends Application {
    private static String FILE_PATH = "Users.txt";
    private static ArrayList<User> users = new ArrayList<>();

    private static int maxAttempts;   // n
    private static int blockSeconds;  // t

    public static ArrayList<User> getUsers() {
        return users;
    }

    public static int getMaxAttempts() {
        return maxAttempts;
    }

    public static int getBlockSeconds() {
        return blockSeconds;
    }

    public static User findUser(String username) {
        for (User u : users) {
            if (u.getUser().equals(username)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lab3/Login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));

        primaryStage.setOnCloseRequest(event -> {
            javafx.application.Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter n (max failed attempts): ");
        maxAttempts = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Enter t (block duration in seconds): ");
        blockSeconds = Integer.parseInt(sc.nextLine().trim());
        System.out.println("n = " + maxAttempts + ", t = " + blockSeconds);

        try {
            BufferedReader r = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            while((line = r.readLine()) != null) {
                try {
                    String[] split = line.split("\\s+");
                    String username = split[0];
                    String password = split[1];
                    User u = new User(username, password);
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
