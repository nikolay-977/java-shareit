package ru.practicum.shareit.server.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.server.user.dto.UserDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @JsonProperty("owner")
    @EqualsAndHashCode.Exclude
    private UserDto ownerDto;
    private Long requestId;
}
