package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.BookingRowMapper;
import ru.practicum.shareit.server.booking.dto.BookingInfoDto;
import ru.practicum.shareit.server.comment.Comment;
import ru.practicum.shareit.server.comment.CommentRepository;
import ru.practicum.shareit.server.comment.CommentRowMapper;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.exception.BadRequestException;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemInfoDto;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.UserRowMapper;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.utils.ShareItPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.server.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRowMapper userRowMapper;
    private final ItemRowMapper itemRowMapper;
    private final CommentRowMapper commentRowMapper;
    private final BookingRowMapper bookingRowMapper;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = getUser(userId);
        Item item = toItem(itemDto);
        item.setOwner(user);

        if (itemDto.getRequestId() != null) {
            item.setItemRequest(itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(REQUEST_NOT_FOUND_MESSAGE)));
        }

        return toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        User user = getUser(userId);
        Item itemToUpdate = getItem(itemId);
        Item item = toItem(itemDto);

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

        return toItemDto(itemRepository.save(itemToUpdate));
    }

    @Override
    public ItemInfoDto getById(Long userId, Long itemId) {
        checkIsUserExist(userId);
        Item item = getItem(itemId);

        List<Comment> commentsList = commentRepository.findAllByItemId(itemId);
        List<Booking> bookingList = bookingRepository.findTop2ByItemOwnerIdAndItemIdOrderByStartAsc(userId, itemId);

        return toItemInfoDto(item, commentsList, bookingList);
    }

    @Override
    public List<ItemInfoDto> getAll(Long userId, Integer from, Integer size) {
        User owner = getUser(userId);
        Pageable pageable = ShareItPageRequest.of(from, size);
        List<Item> itemsByUserId = itemRepository.findAllByOwner(owner, pageable)
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        return getItemInfoDtoList(userId, itemsByUserId);
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        return itemRepository.findAll().stream()
                .filter(item -> !text.isEmpty()
                        && (checkName(item, text) || checkDescription(item, text))
                        && checkAvailable(item)
                )
                .map(this::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User booker = getUser(userId);
        Item item = getItem(itemId);
        bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now()).orElseThrow(() -> new BadRequestException(ACCESS_DENIED_MESSAGE));
        return commentRowMapper.toCommentDto(commentRepository.save(commentRowMapper.toComment(commentDto, booker, item)));
    }

    private void checkIsUserExist(Long id) {
        getUser(id);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_MESSAGE));
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

    private List<ItemInfoDto> getItemInfoDtoList(Long userId, List<Item> itemList) {
        List<ItemInfoDto> itemInfoDtoList = new ArrayList<>();

        for (Item item : itemList) {
            List<Booking> bookingList = bookingRepository.findTop2ByItemOwnerIdAndItemIdOrderByStartAsc(userId, item.getId());
            itemInfoDtoList.add(toItemInfoDto(item, new ArrayList<>(), bookingList));
        }

        return itemInfoDtoList;
    }

    private ItemDto toItemDto(Item item) {
        UserDto ownerDto = userRowMapper.toUserDto(item.getOwner());
        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }
        return itemRowMapper.toItemDto(item, ownerDto, requestId);
    }

    private Item toItem(ItemDto itemDto) {
        User owner = null;
        if (itemDto.getOwnerDto() != null) {
            owner = userRowMapper.toUser(itemDto.getOwnerDto());
        }
        return itemRowMapper.toItem(itemDto, owner);
    }

    private ItemInfoDto toItemInfoDto(Item item, List<Comment> commentsList, List<Booking> bookingList) {
        UserDto ownerDto = userRowMapper.toUserDto(item.getOwner());
        List<CommentDto> commentDtoList = commentsList.stream().map(commentRowMapper::toCommentDto).collect(Collectors.toList());
        List<BookingInfoDto> bookingInfoDtoList = bookingList.stream().map(bookingRowMapper::toBookingInfoDto).collect(Collectors.toList());
        return itemRowMapper.toItemInfoDto(item, ownerDto, commentDtoList, bookingInfoDtoList);
    }

}
