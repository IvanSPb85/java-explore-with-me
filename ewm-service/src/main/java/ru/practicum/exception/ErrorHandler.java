package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.adminApi.category.controller.CategoryController;
import ru.practicum.adminApi.user.controller.UserController;

import java.security.InvalidParameterException;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, CategoryController.class})
public class ErrorHandler {

    class ApiError { //todo
        private String error;
    }

    @ExceptionHandler({DataBaseException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleNotFound(final RuntimeException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBadRequest(final InvalidParameterException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(final MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }


}