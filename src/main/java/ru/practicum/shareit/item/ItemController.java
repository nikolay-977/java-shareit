package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(value = "X-Sharer-User-Id") long userId, @RequestBody @Valid Item item) {
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = "X-Sharer-User-Id") long userId, @RequestBody Item item, @PathVariable long itemId) {
        return itemService.update(userId, item, itemId);
    }

    @GetMapping("/{id}")
    public ItemDto getById(@RequestHeader(value = "X-Sharer-User-Id") long userId, @PathVariable long id) {
        return itemService.getById(userId, id);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(value = "X-Sharer-User-Id") long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        return itemService.search(userId, text);
    }
}
