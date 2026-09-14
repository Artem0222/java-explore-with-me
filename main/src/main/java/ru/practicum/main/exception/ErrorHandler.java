package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException e) {
        log.error("404 not found: {}", e.getMessage());
        return Map.of(
                "status", "NOT FOUND",
                "reason", "The required object was not found.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(BadRequestException e) {
        log.error("400 Bad request: {}", e.getMessage());
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Incorrectyl made requests.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleConflict(ConflictException e) {
        log.error("409 Conflict: {}", e.getMessage());
        return Map.of(
                "status", "CONFLICT",
                "reason", "Integrity constrait has been violated",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException e) {
        log.error("400 validation oerrr: {}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> "Field: " + error.getField() + ". Eroor: " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation eroor");
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Incorrectly made request.",
                "message", message,
                "timestamp", LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleThrowable(Throwable e) {
        log.error("500 internatl server errror: {}", e.getMessage(), e);
        return Map.of(
                "status", "Internal server erorr",
                "reason", "Internal server eorre",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMissingParam(MissingServletRequestParameterException e) {
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER)
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return Map.of(
                "status", "BAD_REQUEST",
                "reason", "Incorrectly made request.",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now().format(FORMATTER)
        );
    }
}





















