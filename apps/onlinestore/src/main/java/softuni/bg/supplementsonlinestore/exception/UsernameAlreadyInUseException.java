package softuni.bg.supplementsonlinestore.exception;

public class UsernameAlreadyInUseException extends RuntimeException
{
    public UsernameAlreadyInUseException(String message) {
        super(message);
    }
}
