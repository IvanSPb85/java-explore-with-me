package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.transfer.PageSort.getPageable;

@Service
@Transactional(readOnly = true)
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        User user = UserMapper.toUser(newUserRequest);
        UserDto userDto;
        try {
            userDto = UserMapper.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(String.format("User with email = '%s' already exists",
                    user.getEmail()), e);
        }
        log.info("User with id = '{}' and email = '{}' successful created",
                userDto.getId(), userDto.getEmail());
        return userDto;
    }

    @Override
    public Collection<UserDto> findUsersByParam(List<Long> ids, Integer from, Integer size) {
        Collection<User> users;
        Pageable pageable = getPageable(from, size, Sort.by(Sort.Direction.ASC, "id"));
        if (ids == null) {
            users = userRepository.findAll(pageable).toList();
        } else {
            users = userRepository.findAllByIdIn(ids, pageable);
        }
        log.info("Found '{}' users", users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new InvalidParameterException(String.format(
                    "Deleting a user with id = '%d' is not possible, user not found", userId));
        }
        log.info("User with id = '{}' deleted", userId);
    }
}
