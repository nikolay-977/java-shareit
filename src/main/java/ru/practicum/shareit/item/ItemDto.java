package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private UserDto owner;
    private ItemRequest request;
}
