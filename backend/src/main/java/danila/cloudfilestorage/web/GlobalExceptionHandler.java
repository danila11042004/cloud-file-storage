package danila.cloudfilestorage.web;

import danila.cloudfilestorage.dto.ErrorResponseDto;
import danila.cloudfilestorage.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;


import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String UNKNOWN_ERROR_MESSAGE = "Неизвестная ошибка";
    private static final String UNKNOWN_ERROR_MESSAGE_LOG = "Неизвестная ошибка в {} {}";

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDto handleInvalidCredentialsException(InvalidCredentialsException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleUserAlreadyExistException(UserAlreadyExistException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(ResourceAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleFileAlreadyExistException(ResourceAlreadyExistException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));
        return new ErrorResponseDto(message);
    }

    @ExceptionHandler(InvalidRequestBodyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidRequestParametersException(InvalidRequestBodyException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(InvalidRequestParametersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidRequestParametersException(InvalidRequestParametersException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleResourceNotFoundException(ResourceNotFoundException exception) {
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(FileStorageAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleFileStorageAccessException(FileStorageAccessException exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(StreamProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleStreamProcessingException(StreamProcessingException exception) {
        log.error(exception.getMessage(), exception);
        return new ErrorResponseDto(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleUnknownException(Exception exception, HttpServletRequest req) {
        log.error(UNKNOWN_ERROR_MESSAGE_LOG, req.getMethod(), req.getRequestURI(), exception);
        return new ErrorResponseDto(UNKNOWN_ERROR_MESSAGE);
    }

}
