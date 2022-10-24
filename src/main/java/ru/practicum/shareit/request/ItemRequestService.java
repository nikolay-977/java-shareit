package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllByUserId(Long userId);

    List<ItemRequestDto> getAllWithPagination(Integer from, Integer size, Long userId);

    ItemRequestDto getItemRequestById(Long userId, Long requestId);
}
