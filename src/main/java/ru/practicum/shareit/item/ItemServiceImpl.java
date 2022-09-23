package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserRowMapper;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(long userId, Item item) {
        return itemRepository.create(getUser(userId), item);
    }

    @Override
    public ItemDto update(long userId, Item item, long itemId) {
        return itemRepository.update(getUser(userId), item, itemId);
    }

    @Override
    public ItemDto getById(long userId, long id) {
        return itemRepository.getById(getUser(userId), id);
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        return itemRepository.getAll(userId);
    }

    @Override
    public List<ItemDto> search(long userId, String text) {
        return itemRepository.search(getUser(userId), text);
    }

    private User getUser(long userId) {
        try {
            return UserRowMapper.toUser(userRepository.getById(userId));
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
