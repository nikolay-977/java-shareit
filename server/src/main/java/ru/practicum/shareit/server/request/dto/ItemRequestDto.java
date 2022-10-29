package ru.practicum.shareit.server.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long ownerId;
    private LocalDateTime created;
    @JsonProperty("items")
    private List<ItemDto> itemDtoList = new ArrayList<>();
}
