package ru.practicum.shareit.server.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.server.booking.BookingState;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingState status;
    @JsonProperty("booker")
    @EqualsAndHashCode.Exclude
    private UserDto bookerDto;
    @JsonProperty("item")
    @EqualsAndHashCode.Exclude
    private ItemDto itemDto;
}
