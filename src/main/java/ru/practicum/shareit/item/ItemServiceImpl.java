package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        return ItemRowMapper.toItemDto(itemRepository.create(getUser(userId), ItemRowMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        return ItemRowMapper.toItemDto(itemRepository.update(getUser(userId), ItemRowMapper.toItem(itemDto), itemId));
    }

    @Override
    public ItemDto getById(Long userId, Long id) {
        return ItemRowMapper.toItemDto(itemRepository.getById(getUser(userId), id));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        final long finalUserId = userId;
        return itemRepository.getAll(userId)
                .stream()
                .filter(i -> i.getOwner().getId() == finalUserId)
                .map(ItemRowMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        return itemRepository.search(getUser(userId), text)
                .stream()
                .map(ItemRowMapper::toItemDto).collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        try {
            return userRepository.getById(userId);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
