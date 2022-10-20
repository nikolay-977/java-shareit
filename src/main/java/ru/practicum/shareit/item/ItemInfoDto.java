package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.booking.BookingInfoDto;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemInfoDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
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
