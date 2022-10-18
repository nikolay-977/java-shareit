package ru.practicum.shareit.request;

import ru.practicum.shareit.user.UserRowMapper;

public class ItemRequestRowMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorDto(itemRequest.getRequestor() != null ? UserRowMapper.toUserDto(itemRequest.getRequestor()) : null)
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(itemRequestDto.getRequestorDto() != null ? UserRowMapper.toUser(itemRequestDto.getRequestorDto()) : null)
                .created(itemRequestDto.getCreated())
                .build();
    }
}
