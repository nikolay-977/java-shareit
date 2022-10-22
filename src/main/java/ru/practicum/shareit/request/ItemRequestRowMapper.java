package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemRowMapper;

import java.util.stream.Collectors;

public class ItemRequestRowMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .ownerId(itemRequest.getOwnerId())
                .itemDtoList(itemRequest.getItemList().stream().map(ItemRowMapper::toItemDto).collect(Collectors.toList()))
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .ownerId(itemRequestDto.getOwnerId())
                .itemList(itemRequestDto.getItemDtoList().stream().map(ItemRowMapper::toItem).collect(Collectors.toList()))
                .created(itemRequestDto.getCreated())
                .build();
    }
}
