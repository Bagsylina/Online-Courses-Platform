package exceptions;

public class FailedLogin extends Exception{
    public FailedLogin(String message) {
        super(message);
    }
}
