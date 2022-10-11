package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto, Long itemId);

    ItemDto getById(Long userId, Long itemId);

    List<ItemDto> getAll(Long userId);

    List<ItemDto> search(Long userId, String text);
}
