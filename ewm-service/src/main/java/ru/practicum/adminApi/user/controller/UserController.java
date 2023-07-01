package ru.practicum.adminApi.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.adminApi.user.dto.UserDto;
import ru.practicum.adminApi.user.dto.NewUserRequest;
import ru.practicum.adminApi.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static ru.practicum.constant.Constant.REQUEST_DELETE_LOG;
import static ru.practicum.constant.Constant.REQUEST_GET_LOG;
import static ru.practicum.constant.Constant.REQUEST_POST_LOG;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid NewUserRequest newUserRequest,
                                          HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(userService.create(newUserRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> findUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(userService.findUsersByParam(ids, from, size), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId, HttpServletRequest request) {
        log.info(REQUEST_DELETE_LOG, request.getRequestURI());
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
