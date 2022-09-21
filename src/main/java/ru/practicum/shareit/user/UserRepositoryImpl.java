package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();

    private final ObjectMapper objectMapper;

    private long userId = 0;

    @Override
    public UserDto create(User user) {
        validateEmail(user.getEmail());
        user.setId(++userId);
        users.add(user);
        return UserRowMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long userId) {
        return UserRowMapper.toUserDto(users.stream()
                .filter(user -> user.getId() == userId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
    }

    @Override
    public List<UserDto> getAll() {
        return users.stream()
                .map(UserRowMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(User user, long userId) {
        validateEmail(user.getEmail());
        User userToUpdate = users.stream()
                .filter(u -> u.getId() == userId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }

        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }

        delete(userToUpdate.getId());
        users.add(userToUpdate);

        return UserRowMapper.toUserDto(userToUpdate);
    }

    public void delete(long userId) {
        users.removeIf(user -> user.getId() == userId);
    }

    private void validateEmail(String email) {
        users.forEach(user -> {
            if (user.getEmail().equals(email)) {
                throw new ConflictException(MessageFormat.format("User with email: {0} already exists", user.getEmail()));
            }
        });
    }
}
