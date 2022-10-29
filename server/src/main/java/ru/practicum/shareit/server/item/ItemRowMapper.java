package ru.practicum.shareit.server.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.dto.BookingInfoDto;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemInfoDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;

@Component
public class ItemRowMapper {
    public ItemDto toItemDto(Item item, UserDto ownerDto, Long requestId) {
        return ItemDto.builder()
                .id(item.getId() != null ? item.getId() : null)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerDto(ownerDto)
                .requestId(requestId)
                .build();
    }

    public ItemInfoDto toItemInfoDto(Item item, UserDto ownerDto, List<CommentDto> commentDtoList, List<BookingInfoDto> bookingInfoDtoList) {
        ItemInfoDto itemInfoDto = ItemInfoDto.builder()
                .id(item.getId() != null ? item.getId() : null)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerDto(ownerDto)
                .commentsDtoList(commentDtoList).build();

        if (bookingInfoDtoList == null || bookingInfoDtoList.size() != 2) {
            return itemInfoDto;
        }

        itemInfoDto.setLastBookingDto(bookingInfoDtoList.get(0));
        itemInfoDto.setNextBookingDto(bookingInfoDtoList.get(1));

        return itemInfoDto;
    }

    public Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId() != null ? itemDto.getId() : null)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }
}
