package softuni.bg.supplementsonlinestore.exception;

public class EmailAlreadyInUserException extends RuntimeException {
    public EmailAlreadyInUserException(String message) {
        super(message);
    }
}
