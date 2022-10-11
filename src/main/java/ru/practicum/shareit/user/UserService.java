package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    UserDto getById(Long id);

    UserDto update(UserDto userDto, Long id);

    void delete(Long id);
}
