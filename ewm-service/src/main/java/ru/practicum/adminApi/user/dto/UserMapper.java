package ru.practicum.adminApi.user.dto;

import ru.practicum.adminApi.user.model.User;

public class UserMapper {
    public static User toUser(NewUserDto newUserDto) {
        return User.builder()
                .name(newUserDto.getName())
                .email(newUserDto.getEmail()).build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }
}
