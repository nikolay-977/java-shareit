package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto updateStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByBookerId(Long userId, String state);

    List<BookingDto> getAllByOwnerId(Long userId, String state);
}
