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

    private Long itemId = 0L;

    @Override
    public Item create(User user, Item item) {
        item.setId(++itemId);
        item.setOwner(user);
        items.add(item);
        return item;
    }

    @Override
    public Item update(User user, Item item, Long id) {
        final long finalId = id;
        Item itemToUpdate = items.stream()
                .filter(i -> i.getId() == finalId)
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

        return itemToUpdate;
    }

    @Override
    public Item getById(User user, Long itemId) {
        final long finalItemId = itemId;
        return items.stream()
                .filter(item -> item.getId() == finalItemId)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    @Override
    public List<Item> getAll(final Long userId) {
        final long finalUserId = userId;
        return items.stream()
                .filter(i -> i.getOwner().getId() == finalUserId)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        final long finalId = id;
        items.removeIf(item -> item.getId() == finalId);
    }

    @Override
    public List<Item> search(User user, String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return items.stream()
                    .filter(item -> item.getDescription() != null
                            && item.getAvailable() != null
                            && item.getDescription().toUpperCase().contains(text.toUpperCase())
                            && item.getAvailable() && !text.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    private void validateUser(User user, Item item) {
        final long userId = user.getId();
        final long ownerId = item.getOwner().getId();
        if (userId != ownerId) {
            throw new NotFoundException("Update with other user");
        }
    }
}
