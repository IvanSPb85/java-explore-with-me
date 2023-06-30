package ru.practicum.adminApi.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.adminApi.user.dao.UserRepository;
import ru.practicum.adminApi.user.dto.UserDto;
import ru.practicum.adminApi.user.dto.UserMapper;
import ru.practicum.adminApi.user.dto.UserShortDto;
import ru.practicum.adminApi.user.model.User;
import ru.practicum.exception.DataBaseException;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserShortDto userShortDto) {
        User user = UserMapper.toUser(userShortDto);
        UserDto userDto;
        try {
            userDto = UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.warn("User with email = {} already exists", user.getEmail());
            throw new DataBaseException(String.format("User with email = %s already exists",
                    user.getEmail()));
        }
        log.info("User with id = {} and email = {} successful created",
                userDto.getId(), userDto.getEmail());
        return userDto;
    }

    @Override
    public Collection<UserDto> findUsersByParam(List<Long> ids, Integer from, Integer size) {
        Collection<User> users;
        if (from > 0 && size > 0) from = from / size;
        PageRequest request = PageRequest.of(from, size,
                Sort.by(Sort.Direction.ASC, "id"));
        if (ids == null) {
            users = userRepository.findAll(request).toList();
        } else {
            users = userRepository.findAllByIdIn(ids, request);
        }
        log.info("Found {} users", users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            log.info("User with id = {} deleted", userId);
        } else {
            log.warn("Deleting a user with id = {} is not possible, user not found", userId);
            throw new InvalidParameterException(String.format(
                    "Deleting a user with id = %s is not possible, user not found", userId));
        }
    }
}
