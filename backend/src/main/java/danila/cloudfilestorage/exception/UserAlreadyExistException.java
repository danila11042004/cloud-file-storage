package danila.cloudfilestorage.exception;

public class UserAlreadyExistException extends RuntimeException {
    public static final String MESSAGE = "Пользователь с таким именем уже существует";

    public UserAlreadyExistException() {
        super(MESSAGE);
    }
}
