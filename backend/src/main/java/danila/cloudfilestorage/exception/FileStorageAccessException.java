package danila.cloudfilestorage.exception;

public class FileStorageAccessException extends RuntimeException {
    public static final String MESSAGE = "Ошибка при обращении к хранилищу файлов";

    public FileStorageAccessException(Exception e) {
        super(MESSAGE, e);
    }
}
