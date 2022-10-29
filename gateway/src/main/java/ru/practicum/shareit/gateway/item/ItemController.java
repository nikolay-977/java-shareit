package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.comment.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @RequestBody @Validated ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemDto itemDto,
                                         @PathVariable Long itemId) {
        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                          @PathVariable Long id) {
        return itemClient.getById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam String text) {
        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Validated CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
