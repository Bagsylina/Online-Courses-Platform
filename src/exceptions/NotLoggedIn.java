package exceptions;

public class NotLoggedIn extends Exception{
    public NotLoggedIn(String message) {
        super(message);
    }
}
