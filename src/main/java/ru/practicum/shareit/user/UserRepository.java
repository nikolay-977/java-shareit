package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    User create(User user);

    User getById(Long id);

    User update(User user, Long id);

    void delete(Long id);
}
