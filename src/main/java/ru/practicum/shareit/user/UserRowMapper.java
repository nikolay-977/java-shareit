package ru.practicum.shareit.user;

public class UserRowMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId() != null ? user.getId() : null)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId() != null ? userDto.getId() : null)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}
