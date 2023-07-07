package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.category.controller.adminApi.CategoryController;
import ru.practicum.category.controller.publicApi.PublicCategoryController;
import ru.practicum.compilation.controller.adminApi.CompilationController;
import ru.practicum.event.controller.adminApi.AdminEventController;
import ru.practicum.event.controller.privateApi.EventController;
import ru.practicum.event.controller.publicApi.PublicEventController;
import ru.practicum.request.controller.privateApi.RequestController;
import ru.practicum.user.controller.adminApi.UserController;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class, CategoryController.class,
        AdminEventController.class, EventController.class, RequestController.class,
        CompilationController.class, PublicCategoryController.class, PublicEventController.class})
public class ErrorHandler {

    @Builder
    @Setter
    @Getter
    static class ApiError { //todo
        private HttpStatus status;
        private String reason;
        private String message;
        @Builder.Default
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime timestamp = LocalDateTime.now();
        private String error;
    }

//    @ExceptionHandler({DataIntegrityViolationException.class, ConflictException.class})
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public Map<String, String> handleNotFound(final RuntimeException e) {
//        log.warn(e.getMessage());
//        return Map.of("error", e.getMessage());
//    }
//
//
//    @ExceptionHandler({NoSuchElementException.class, InvalidParameterException.class})
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public Map<String, String> handleBadRequest(final RuntimeException e) {
//        log.warn(e.getMessage());
//        return Map.of("error", e.getMessage());
//    }
//
//    @ExceptionHandler({DataBaseException.class, MethodArgumentNotValidException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public Map<String, String> handleBadRequest(final Exception e) {
//        log.warn(e.getMessage());
//        return Map.of("error", e.getMessage());
//    }
//
//    @ExceptionHandler({IllegalArgumentException.class})
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ApiError handleBadRequest(final Exception e) {
//        log.error(e.getMessage());
//        StringWriter out = new StringWriter();
//        e.printStackTrace(new PrintWriter(out));
//        String stackTrace = out.toString();
//        return ApiError.builder()
//                .error(stackTrace)
//                .message(e.getMessage())
//                .reason(e.getLocalizedMessage())
//                .status(HttpStatus.BAD_REQUEST)
//                .timestamp(LocalDateTime.now()).build();
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final ConflictException e) {
        log.error("Error: " + e.getLocalizedMessage());
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .error(stackTrace).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final DataIntegrityViolationException e) {
        log.error("Error: " + e.getLocalizedMessage());
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getMessage())
                .status(HttpStatus.CONFLICT)
                .error(stackTrace).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final MethodArgumentNotValidException e) {
        log.error("Error: " + e.getLocalizedMessage());
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .error(stackTrace).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final DataBaseException e) {
        log.error("Error: " + e.getLocalizedMessage());
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .error(stackTrace).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NoSuchElementException e) {
        log.error("Error: " + e.getLocalizedMessage());
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .error(stackTrace).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final InvalidParameterException e) {
        log.error("Error: " + e.getLocalizedMessage());
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .error(stackTrace).build();
    }
}