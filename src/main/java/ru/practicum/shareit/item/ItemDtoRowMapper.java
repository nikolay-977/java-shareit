package ru.practicum.shareit.item;

import ru.practicum.shareit.user.UserRowMapper;

public class ItemDtoRowMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                UserRowMapper.toUserDto(item.getOwner()),
                item.getRequest()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner() != null ? UserRowMapper.toUser(itemDto.getOwner()) : null,
                itemDto.getRequest()
        );
    }
}
