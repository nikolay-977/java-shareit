package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRowMapper;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.UserRowMapper;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.utils.ShareItPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.server.exception.ErrorMessage.REQUEST_NOT_FOUND_MESSAGE;
import static ru.practicum.shareit.server.exception.ErrorMessage.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final UserRowMapper userRowMapper;
    private final ItemRowMapper itemRowMapper;
    private final ItemRequestRowMapper itemRequestRowMapper;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        checkIsUserExist(userId);
        itemRequestDto.setOwnerId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        return toItemRequestDto(itemRequestRepository.save(toItemRequest(itemRequestDto)));
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        checkIsUserExist(userId);
        return itemRequestRepository.findAllByOwnerIdOrderByCreated(userId).stream().map(this::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllWithPagination(Integer from, Integer size, Long userId) {
        checkIsUserExist(userId);
        Pageable pageable = ShareItPageRequest.of(from, size);
        return itemRequestRepository.findAllByIdIsNotOrderByCreated(userId, pageable).stream().map(this::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        checkIsUserExist(userId);
        return toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(REQUEST_NOT_FOUND_MESSAGE)));
    }

    private void checkIsUserExist(Long id) {
        userRowMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE)));
    }

    private ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        List<Item> itemList = itemRequestDto.getItemDtoList().stream().map(this::toItem).collect(Collectors.toList());
        return itemRequestRowMapper.toItemRequest(itemRequestDto, itemList);
    }

    private ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> itemDtoList = itemRequest.getItemList().stream().map(this::toItemDto).collect(Collectors.toList());
        return itemRequestRowMapper.toItemRequestDto(itemRequest, itemDtoList);
    }

    private Item toItem(ItemDto itemDto) {
        User owner = null;
        if (itemDto.getOwnerDto() != null) {
            owner = userRowMapper.toUser(itemDto.getOwnerDto());
        }
        return itemRowMapper.toItem(itemDto, owner);
    }

    private ItemDto toItemDto(Item item) {
        UserDto ownerDto = userRowMapper.toUserDto(item.getOwner());
        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }
        return itemRowMapper.toItemDto(item, ownerDto, requestId);
    }
}
