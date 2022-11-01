package ru.practicum.shareit.server.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.user.dto.UserDto;

@Component
public class UserRowMapper {
    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId() != null ? user.getId() : null)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId() != null ? userDto.getId() : null)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
