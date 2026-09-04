package danila.cloudfilestorage.exception;

public class InvalidCredentialsException extends RuntimeException {
    public static final String MESSAGE = "Неверные веденные учетные данные";

    public InvalidCredentialsException() {
        super(MESSAGE);
    }
}
