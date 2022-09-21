package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {
    ItemDto create(User user, Item item);

    ItemDto update(User user, Item item, long itemId);

    ItemDto getById(User user, long itemId);

    List<ItemDto> getAll(long userId);

    void delete(long id);

    List<ItemDto> search(User user, String text);
}
