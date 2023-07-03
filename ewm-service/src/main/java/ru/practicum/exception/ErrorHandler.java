package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.category.controller.adminApi.CategoryController;
import ru.practicum.event.controller.adminApi.AdminEventController;
import ru.practicum.user.controller.adminApi.UserController;
import ru.practicum.event.controller.privateApi.EventController;

import java.security.InvalidParameterException;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, CategoryController.class,
        AdminEventController.class, EventController.class})
public class ErrorHandler {

    class ApiError { //todo
        private String error;
    }

    @ExceptionHandler({DataBaseException.class, DataIntegrityViolationException.class, ConflictException.class})
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