package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
