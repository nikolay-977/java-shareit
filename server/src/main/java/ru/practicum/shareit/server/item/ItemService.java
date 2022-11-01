package ru.practicum.shareit.server.item;

import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemInfoDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto, Long itemId);

    ItemInfoDto getById(Long userId, Long itemId);

    List<ItemInfoDto> getAll(Long userId, Integer from, Integer size);

    List<ItemDto> search(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
