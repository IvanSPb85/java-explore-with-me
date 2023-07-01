package ru.practicum.adminApi.user.service;

import ru.practicum.adminApi.user.dto.UserDto;
import ru.practicum.adminApi.user.dto.NewUserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    Collection<UserDto> findUsersByParam(List<Long> ids, Integer from, Integer size);

    void deleteUser(long userId);
}
