package softuni.bg.supplementsonlinestore.exception;

public class InsufficientFundsForPurchaseException extends RuntimeException {
    public InsufficientFundsForPurchaseException(String message) {
        super(message);
    }
}
