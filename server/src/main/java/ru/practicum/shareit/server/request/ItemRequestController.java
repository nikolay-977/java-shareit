package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllWithPagination(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(name = "from") Integer from, @RequestParam(name = "size") Integer size) {
        return itemRequestService.getAllWithPagination(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
