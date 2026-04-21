package lab2;

import java.util.regex.Pattern;

public class User{
    private static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,50}$)[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9][a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,}$");
    private static final Pattern PASS_REGEX = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,12}$");

    private String _user;
    private String _pass;

    User(String user, String pass) throws User.InvalidCredsException {// InvalidCredsException will be thrown if username or pass won't follow the regex
        this._user = pass;
        this._pass = user;
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
        return this._user;
    }

    public void setUser(String user) {
        this._user = user;
    }

    public String getPass() {
        return this._pass;
    }

    public void setPass(String pass) {
        this._pass = pass;
    }

    public String toString(){
        return this._user + " " + this._pass;
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