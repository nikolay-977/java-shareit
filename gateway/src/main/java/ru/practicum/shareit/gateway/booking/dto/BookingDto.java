package ru.practicum.shareit.gateway.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.gateway.item.dto.ItemDto;
import ru.practicum.shareit.gateway.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
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
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private BookingState status;
    @JsonProperty("booker")
    @EqualsAndHashCode.Exclude
    private UserDto bookerDto;
    @JsonProperty("item")
    @EqualsAndHashCode.Exclude
    private ItemDto itemDto;
}
