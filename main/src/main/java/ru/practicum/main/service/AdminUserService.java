package ru.practicum.main.service;

import ru.practicum.main.dto.user.NewUserRequest;
import ru.practicum.main.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {

    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}