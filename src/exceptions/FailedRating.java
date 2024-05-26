package exceptions;

public class FailedRating extends Exception{
    public FailedRating(String message) {
        super(message);
    }
}
