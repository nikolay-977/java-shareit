package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {
    Item create(User user, Item item);

    Item update(User user, Item item, Long itemId);

    Item getById(User user, Long itemId);

    List<Item> getAll(Long userId);

    void delete(Long id);

    List<Item> search(User user, String text);
}
