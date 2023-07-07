package ru.practicum.user.service;

import ru.practicum.exception.ConflictException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest) throws ConflictException;

    Collection<UserDto> findUsersByParam(List<Long> ids, Integer from, Integer size);

    void deleteUser(long userId);
}
