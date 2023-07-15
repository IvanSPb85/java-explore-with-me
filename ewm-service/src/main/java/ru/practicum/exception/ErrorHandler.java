package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static ru.practicum.constant.Constant.YYYY_MM_DD_HH_MM_SS;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum")
public class ErrorHandler {

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(final RuntimeException e) {
        log.error("Error: " + e.getLocalizedMessage());
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getCause() != null ? e.getCause().getMessage() : null)
                .status(HttpStatus.CONFLICT)
                .error(getStackTrace(e)).build();
    }

    @ExceptionHandler({ValidDateException.class,
            ConstraintViolationException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final RuntimeException e) {
        log.error("Error: " + e.getLocalizedMessage());
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getCause() != null ? e.getCause().getMessage() : null)
                .status(HttpStatus.BAD_REQUEST)
                .error(getStackTrace(e)).build();
    }

    @ExceptionHandler({NoSuchElementException.class, InvalidParameterException.class,
            EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final RuntimeException e) {
        log.error("Error: " + e.getLocalizedMessage());
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getCause() != null ? e.getCause().getMessage() : null)
                .status(HttpStatus.NOT_FOUND)
                .error(getStackTrace(e)).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleServerError(final Throwable e) {
        log.error("Error: " + e.getLocalizedMessage());
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getCause() != null ? e.getCause().getMessage() : null)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .error(getStackTrace(e)).build();
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBsdReaThrow(final Throwable e) {
        log.error("Error: " + e.getLocalizedMessage());
        return ApiError.builder()
                .reason(e.getLocalizedMessage())
                .message(e.getCause() != null ? e.getCause().getMessage() : null)
                .status(HttpStatus.BAD_REQUEST)
                .error(getStackTrace(e)).build();
    }

    @Builder
    @Setter
    @Getter
    static class ApiError {
        private HttpStatus status;
        private String reason;
        private String message;
        @Builder.Default
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = YYYY_MM_DD_HH_MM_SS)
        private LocalDateTime timestamp = LocalDateTime.now();
        private String error;
    }

    private String getStackTrace(Throwable e) {
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        return out.toString();
    }
}