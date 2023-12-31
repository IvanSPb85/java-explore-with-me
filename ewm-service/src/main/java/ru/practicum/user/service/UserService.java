package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);

    Collection<UserDto> findUsersByParam(List<Long> ids, Integer from, Integer size);

    void deleteUser(long userId);
}
