package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotEmpty;
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
    @NotEmpty
    private String description;
    private Long ownerId;
    private LocalDateTime created;
    @JsonProperty("items")
    private List<ItemDto> itemDtoList = new ArrayList<>();
}
