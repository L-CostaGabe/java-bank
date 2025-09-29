package project.bank.exception;

public class PixInUseException  extends RuntimeException {
    public PixInUseException(String message) {
        super(message);
    }
}
