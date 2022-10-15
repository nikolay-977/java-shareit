package ru.practicum.shareit.request;

import ru.practicum.shareit.user.UserRowMapper;

public class ItemRequestRowMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor() != null ? UserRowMapper.toUserDto(itemRequest.getRequestor()) : null,
                itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequestorDto() != null ? UserRowMapper.toUser(itemRequestDto.getRequestorDto()) : null,
                itemRequestDto.getCreated()
        );
    }
}
