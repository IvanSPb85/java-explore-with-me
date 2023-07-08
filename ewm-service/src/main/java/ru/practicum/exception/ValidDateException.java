package ru.practicum.exception;

public class ValidDateException extends RuntimeException {
    public ValidDateException(String message) {
        super(message);
    }
}
