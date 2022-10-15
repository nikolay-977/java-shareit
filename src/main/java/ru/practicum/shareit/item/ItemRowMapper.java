package ru.practicum.shareit.item;

import ru.practicum.shareit.request.ItemRequestRowMapper;
import ru.practicum.shareit.user.UserRowMapper;

public class ItemRowMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? UserRowMapper.toUserDto(item.getOwner()) : null,
                item.getRequest() != null ? ItemRequestRowMapper.toItemRequestDto(item.getRequest()) : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwnerDto() != null ? UserRowMapper.toUser(itemDto.getOwnerDto()) : null,
                itemDto.getRequestDto() != null ? ItemRequestRowMapper.toItemRequest(itemDto.getRequestDto()) : null
        );
    }
}
