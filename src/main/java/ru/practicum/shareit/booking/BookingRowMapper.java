package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.ItemRowMapper;
import ru.practicum.shareit.user.UserRowMapper;

public class BookingRowMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerDto(UserRowMapper.toUserDto(booking.getBooker()))
                .itemDto(ItemRowMapper.toItemDto(booking.getItem()))
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(bookingDto.getStatus())
                .booker(UserRowMapper.toUser(bookingDto.getBookerDto()))
                .item(ItemRowMapper.toItem(bookingDto.getItemDto()))
                .build();
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        return BookingInfoDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
