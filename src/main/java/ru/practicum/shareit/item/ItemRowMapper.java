package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRowMapper;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRowMapper;
import ru.practicum.shareit.user.UserRowMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRowMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId() != null ? item.getId() : null)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerDto(item.getOwner() != null ? UserRowMapper.toUserDto(item.getOwner()) : null)
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .build();
    }

    public static ItemInfoDto toItemInfoDto(Item item, List<Booking> bookingList, List<Comment> commentList) {
        List<CommentDto> commentDtoList = commentList.stream().map(CommentRowMapper::toCommentDto).collect(Collectors.toList());
        ItemInfoDto itemInfoDto = ItemInfoDto.builder()
                .id(item.getId() != null ? item.getId() : null)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerDto(item.getOwner() != null ? UserRowMapper.toUserDto(item.getOwner()) : null)
                .commentsDtoList(commentDtoList).build();

        if (bookingList == null || bookingList.size() != 2) {
            return itemInfoDto;
        }

        Booking lastBooking = bookingList.get(0);
        Booking nextBooking = bookingList.get(1);

        itemInfoDto.setLastBookingDto(BookingRowMapper.toBookingInfoDto(lastBooking));
        itemInfoDto.setNextBookingDto(BookingRowMapper.toBookingInfoDto(nextBooking));

        return itemInfoDto;
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId() != null ? itemDto.getId() : null)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwnerDto() != null ? UserRowMapper.toUser(itemDto.getOwnerDto()) : null)
                .build();
    }
}
