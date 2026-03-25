package lab1;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;


public class UserApp {
    private static String FILE_PATH = "Users.txt";
    private static ArrayList<User> users = new ArrayList<>();

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
    }
}
