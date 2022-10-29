package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {

        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        return bookingService.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    private List<BookingDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) {
        return bookingService.getAllByOwnerId(userId, state, from, size);
    }
}
