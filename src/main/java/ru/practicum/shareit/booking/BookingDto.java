package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Long bookerId;
    private String status;
}
