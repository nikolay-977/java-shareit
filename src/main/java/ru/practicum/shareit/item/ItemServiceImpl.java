package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentRowMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = getUser(userId);
        Item item = ItemRowMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemRowMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        User user = getUser(userId);
        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("The item not found"));
        Item item = ItemRowMapper.toItem(itemDto);

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

        return ItemRowMapper.toItemDto(itemRepository.save(itemToUpdate));
    }

    @Override
    @Transactional
    public ItemInfoDto getById(Long userId, Long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_MESSAGE));

        List<Booking> bookingList = bookingRepository.findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(userId, itemId);
        List<Comment> commentsList = commentRepository.findAllByItem_Id(itemId);

        return ItemRowMapper.toItemInfoDto(item, bookingList, commentsList);
    }

    @Override
    @Transactional
    public List<ItemInfoDto> getAll(Long userId) {
        final long finalUserId = userId;
        User owner = userRepository.findById(finalUserId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        List<Item> itemsByUserId = itemRepository.findAllByOwner(owner);
        return getItemInfoDtoList(userId, itemsByUserId);
    }

    @Override
    @Transactional
    public List<ItemDto> search(Long userId, String text) {
        return itemRepository.findAll().stream()
                .filter(item -> !text.isEmpty()
                        && (checkName(item, text) || checkDescription(item, text))
                        && checkAvailable(item)
                )
                .map(ItemRowMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_MESSAGE));
        bookingRepository.findFirstByBooker_IdAndItem_IdAndEndBefore(userId, itemId, LocalDateTime.now()).orElseThrow(() -> new BadRequestException(ACCESS_DENIED_MESSAGE));
        return CommentRowMapper.toCommentDto(commentRepository.save(CommentRowMapper.toComment(commentDto, booker, item)));

    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    private void validateUser(User user, Item item) {
        final long userId = user.getId();
        final long ownerId = item.getOwner().getId();
        if (userId != ownerId) {
            throw new NotFoundException("Update with other user");
        }
    }

    private Boolean checkName(Item item, String text) {
        return item.getName() != null
                && (item.getName().toUpperCase()).contains(text.toUpperCase());
    }

    private Boolean checkDescription(Item item, String text) {
        return item.getDescription() != null
                && (item.getDescription().toUpperCase()).contains(text.toUpperCase());
    }

    private Boolean checkAvailable(Item item) {
        return item.getAvailable() != null
                && item.getAvailable();
    }

    private List<ItemInfoDto> getItemInfoDtoList(Long userId, List<Item> itemsByUserId) {
        List<ItemInfoDto> itemInfoDtoList = new ArrayList<>();

        for (Item item : itemsByUserId) {
            List<Booking> bookingList = bookingRepository.findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(userId, item.getId());
            itemInfoDtoList.add(ItemRowMapper.toItemInfoDto(item, bookingList, new ArrayList<>()));
        }

        return itemInfoDtoList;
    }
}
