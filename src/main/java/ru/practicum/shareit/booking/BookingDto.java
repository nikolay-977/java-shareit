package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
@ */
@Data
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private long bookerId;
    private String status;
}
