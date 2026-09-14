package ru.practicum.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.model.User;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.util.PageRequestUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService   {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("Пользователь с емейлом " + newUserRequest.getEmail() + " уже существует");
        }

        User user = userMapper.toEntity(newUserRequest);
        user = userRepository.save(user);
        log.info("Пользователь добавлен: {}", user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequestUtil.of(from, size);

        List<User> users;
        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findAllById(ids);
        } else {
            users = userRepository.findAll(pageable).getContent();
        }

        log.info("Отображение {} пользователей", users.size());
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("Пользователь с ид " + userId + " не найден"));

                userRepository.delete(user);
                log.info("ПОльзователь с ид={} удалён", userId);
    }
}
