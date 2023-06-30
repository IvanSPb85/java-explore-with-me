package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.controller.StatsController;

import java.security.InvalidParameterException;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = StatsController.class)
public class ErrorHandler {

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final InvalidParameterException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }
}
