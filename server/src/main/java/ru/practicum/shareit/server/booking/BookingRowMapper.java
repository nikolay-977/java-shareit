package ru.practicum.shareit.server.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingInfoDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;

@Component
public class BookingRowMapper {
    public BookingDto toBookingDto(Booking booking, UserDto bookerDto, ItemDto itemDto) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerDto(bookerDto)
                .itemDto(itemDto)
                .build();
    }

    public Booking toBooking(BookingDto bookingDto, User booker, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .booker(booker)
                .item(item)
                .build();
    }

    public BookingInfoDto toBookingInfoDto(Booking booking) {
        return BookingInfoDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
