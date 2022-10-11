package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final List<User> users = new ArrayList<>();

    private final ObjectMapper objectMapper;

    private Long userId = 0L;

    @Override
    public User create(User user) {
        validateEmail(user.getEmail());
        user.setId(++userId);
        users.add(user);
        return user;
    }

    @Override
    public User getById(Long userId) {
        final long finalUserId = userId;
        return users.stream()
                .filter(user -> user.getId() == finalUserId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public User update(User user, Long userId) {
        validateEmail(user.getEmail());
        final long finaUserId = userId;
        User userToUpdate = users.stream()
                .filter(u -> u.getId() == finaUserId)
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

        return userToUpdate;
    }

    public void delete(Long userId) {
        final long finalUserId = userId;
        users.removeIf(user -> user.getId() == finalUserId);
    }

    private void validateEmail(String email) {
        users.forEach(user -> {
            if (Objects.equals(user.getEmail(), email)) {
                throw new ConflictException(MessageFormat.format("User with email: {0} already exists", user.getEmail()));
            }
        });
    }
}
