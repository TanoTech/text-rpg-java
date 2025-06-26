package rpg.exceptions;

public class ValidationException extends GameException {
    public ValidationException(String message) {
        super("Invalid input: " + message, message);
    }
}