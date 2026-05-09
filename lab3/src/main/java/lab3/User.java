package lab3;

import java.util.regex.Pattern;

public class User{
    private static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,50}$)[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9][a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,}$");
    private static final Pattern PASS_REGEX = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z0-9!@#$%^&*()_+]{8,12}$");

    private String _user;
    private String _pass;
    private int _failedAttempts = 0;
    private long _blockedAt = 0; // 0 means not blocked, otherwise the time we blocked the user

    User(String user, String pass) throws User.InvalidCredsException {// InvalidCredsException will be thrown if username or pass won't follow the regex
        this._user = user;
        this._pass = pass;
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

    public synchronized int getFailedAttempts() {
        return _failedAttempts;
    }

    public synchronized void resetFailedAttempts() {
        _failedAttempts = 0;
    }

    public synchronized long getBlockedAt() {
        return _blockedAt;
    }

    // Called when the user got the password wrong. Adds 1 to the failed counter,
    // and blocks the user if we hit the max. Returns the new failed count.
    public synchronized int fail() {
        if (isBlocked()) return _failedAttempts; // already blocked, do nothing
        _failedAttempts++;
        if (_failedAttempts >= UserApp.getMaxAttempts()) {
            _blockedAt = System.currentTimeMillis();
        }
        return _failedAttempts;
    }

    // Called when the user got the password right.
    // Returns false if the user is blocked, otherwise resets the counter and returns true.
    public synchronized boolean succeed() {
        if (isBlocked()) return false;
        _failedAttempts = 0;
        return true;
    }

    public synchronized boolean isBlocked() {
        if (_blockedAt == 0) return false;
        // if t seconds passed since we blocked - the block is over, reset everything
        if (System.currentTimeMillis() - _blockedAt >= UserApp.getBlockSeconds() * 1000L) {
            _blockedAt = 0;
            _failedAttempts = 0;
            return false;
        }
        return true;
    }

    // how many seconds left on the block (0 if not blocked)
    public synchronized long remainingBlockSeconds() {
        if (_blockedAt == 0) return 0;
        long remainMs = UserApp.getBlockSeconds() * 1000L - (System.currentTimeMillis() - _blockedAt);
        return Math.max(0, (remainMs + 999) / 1000); // round up to whole seconds
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