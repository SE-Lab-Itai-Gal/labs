package lab2;

import java.util.regex.Pattern;

public class User{
    private static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,50}$)[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9][a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,}$");
    private static final Pattern PASS_REGEX = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,12}$");

    private String user;
    private String pass;

    User(String email_pass) throws User.InvalidCredsException {// InvalidCredsException will be thrown if username or pass won't follow the regex
        String[] split = email_pass.split("\\s+");
        this.user = split[0];
        this.pass = split[1];
        if(user.length() > 50){
            throw new InvalidUsernameException("Username is too long, try something shorter");
        }
        if(!EMAIL_REGEX.matcher(user).matches()){ // Check the username follows the regex
            throw new InvalidUsernameException("Please enter a valid Email as username");
        }
        if(pass.length() < 8){
            throw new InvalidPasswordException("Your password is too short, add more characters");
        }
        if(pass.length() > 12){
            throw new InvalidPasswordException("Your password is too long, try a shorter one");
        }
        if(!PASS_REGEX.matcher(pass).matches()){ // Check the password follows the regex
            throw new InvalidPasswordException("Please enter a valid password");
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String toString(){
        return user + " " + pass;
    }

    public static class InvalidCredsException extends Exception {
        public InvalidCredsException(String message) {
            super(message);
        }
    }

    public static class InvalidUsernameException extends InvalidCredsException {
        public InvalidUsernameException(String message) {
            super(message);
        }
    }

    public static class InvalidPasswordException extends InvalidCredsException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }
}