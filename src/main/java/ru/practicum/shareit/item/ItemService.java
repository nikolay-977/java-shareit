package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(long userId, Item item);

    ItemDto update(long userId, Item item, long itemId);

    ItemDto getById(long userId, long itemId);

    List<ItemDto> getAll(long userId);

    List<ItemDto> search(long userId, String text);
}
