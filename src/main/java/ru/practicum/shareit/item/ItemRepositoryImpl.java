package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final List<Item> items = new ArrayList<>();

    private final ObjectMapper objectMapper;

    private long itemId = 0;

    @Override
    public ItemDto create(User user, Item item) {
        item.setId(++itemId);
        item.setOwner(user);
        items.add(item);
        return ItemDtoRowMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(User user, Item item, long id) {
        Item itemToUpdate = items.stream()
                .filter(i -> i.getId() == id)
                .findAny()
                .orElseThrow(() -> new NotFoundException("Item not found"));

        validateUser(user, itemToUpdate);

        if (item.getOwner() != null) {
            itemToUpdate.setOwner(item.getOwner());
        }

        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }

        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }

        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }

        delete(itemToUpdate.getId());
        items.add(itemToUpdate);

        return ItemDtoRowMapper.toItemDto(itemToUpdate);
    }

    @Override
    public ItemDto getById(User user, long itemId) {
        Item item = items.stream()
                .filter(i -> i.getId() == itemId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        return ItemDtoRowMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        return items.stream()
                .filter(i -> i.getOwner().getId() == userId)
                .map(ItemDtoRowMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(long id) {
        items.removeIf(item -> item.getId() == id);
    }

    @Override
    public List<ItemDto> search(User user, String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else return items.stream()
                .filter(i -> i.getDescription().toUpperCase().contains(text.toUpperCase()) && i.getAvailable() && !text.isEmpty())
                .map(ItemDtoRowMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateUser(User user, Item item) {
        if (user.getId() != item.getOwner().getId()) {
            throw new NotFoundException("Update with other user");
        }
    }
}
