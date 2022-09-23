package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto create(User user);

    UserDto getById(long id);

    UserDto update(User user, long id);

    void delete(long id);
}
