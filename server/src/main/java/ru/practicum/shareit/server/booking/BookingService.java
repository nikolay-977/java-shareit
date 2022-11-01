package ru.practicum.shareit.server.booking;

import ru.practicum.shareit.server.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByBookerId(Long userId, String state, Integer from, Integer size);

    List<BookingDto> getAllByOwnerId(Long userId, String state, Integer from, Integer size);
}
