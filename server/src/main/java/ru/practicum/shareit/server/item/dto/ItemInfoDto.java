package ru.practicum.shareit.server.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.server.booking.dto.BookingInfoDto;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemInfoDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @JsonProperty("owner")
    private UserDto ownerDto;
    @JsonProperty("lastBooking")
    private BookingInfoDto lastBookingDto;
    @JsonProperty("nextBooking")
    private BookingInfoDto nextBookingDto;
    @JsonProperty("comments")
    private List<CommentDto> commentsDtoList;
}
