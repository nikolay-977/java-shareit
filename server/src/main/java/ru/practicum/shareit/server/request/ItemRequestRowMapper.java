package ru.practicum.shareit.server.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;

@Component
public class ItemRequestRowMapper {
    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemDto> itemDtoList) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .ownerId(itemRequest.getOwnerId())
                .itemDtoList(itemDtoList)
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, List<Item> itemList) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .ownerId(itemRequestDto.getOwnerId())
                .itemList(itemList)
                .created(itemRequestDto.getCreated())
                .build();
    }
}
