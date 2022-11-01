package ru.practicum.shareit.server.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.exception.BadRequestException;
import ru.practicum.shareit.server.exception.ErrorMessage;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.ItemRowMapper;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.UserRowMapper;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.utils.ShareItPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.server.booking.BookingState.*;
import static ru.practicum.shareit.server.exception.ErrorMessage.ACCESS_DENIED_MESSAGE;
import static ru.practicum.shareit.server.exception.ErrorMessage.IS_APPROVED_ALREADY_SET;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRowMapper bookingRowMapper;
    private final ItemRowMapper itemRowMapper;
    private final UserRowMapper userRowMapper;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        final long finalUserId = userId;

        UserDto bookerDto = getUserDto(finalUserId);
        ItemDto itemDto = getItemDto(bookingDto.getItemId());

        if (!itemDto.getAvailable()) {
            throw new BadRequestException(ErrorMessage.ITEM_NOT_AVAILABLE);
        }
        if (itemDto.getOwnerDto().getId() == finalUserId) {
            throw new NotFoundException(ACCESS_DENIED_MESSAGE);
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new BadRequestException(ErrorMessage.START_DATE_SHOULD_BE_EARLIER);
        }

        bookingDto.setBookerDto(bookerDto);
        bookingDto.setItemDto(itemDto);
        bookingDto.setStatus(WAITING);

        return toBookingDto(bookingRepository.save(toBooking(bookingDto)));

    }

    @Override
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean approved) {
        checkIsUserExist(userId);
        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId))
            throw new NotFoundException(ACCESS_DENIED_MESSAGE);

        BookingState newStatus = approved ? APPROVED : REJECTED;

        if (newStatus == APPROVED && booking.getStatus() == APPROVED) {
            throw new BadRequestException(IS_APPROVED_ALREADY_SET);
        } else {
            booking.setStatus(newStatus);
        }

        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        checkIsUserExist(userId);
        Booking booking = getBooking(bookingId);

        boolean isUserIdBooker = booking.getBooker().getId().equals(userId);
        boolean isUserIdOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!(isUserIdBooker || isUserIdOwner)) {
            throw new NotFoundException(ACCESS_DENIED_MESSAGE);
        }

        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBookerId(Long userId, String state, Integer from, Integer size) {
        checkIsUserExist(userId);
        Pageable pageable = ShareItPageRequest.of(from, size);
        BookingState bookingState = getState(state);
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL: {
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
                break;
            }
            case APPROVED:
            case WAITING:
            case REJECTED: {
                bookingList = bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(userId, bookingState, pageable);
                break;
            }
            case FUTURE: {
                bookingList = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case PAST: {
                bookingList = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case CURRENT: {
                bookingList = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            }
        }

        return bookingList.stream().map(this::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwnerId(Long userId, String state, Integer from, Integer size) {
        checkIsUserExist(userId);
        Pageable pageable = ShareItPageRequest.of(from, size);
        BookingState bookingState = getState(state);
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL: {
                bookingList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId, pageable);
                break;
            }
            case APPROVED:
            case WAITING:
            case REJECTED: {
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(userId, bookingState, pageable);
                break;
            }
            case FUTURE: {
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case PAST: {
                bookingList = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case CURRENT: {
                bookingList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            }
        }

        return bookingList.stream().map(this::toBookingDto).collect(Collectors.toList());
    }

    private BookingState getState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format(ErrorMessage.UNKNOWN_STATE, state));
        }
    }

    private void checkIsUserExist(Long id) {
        getUserDto(id);
    }

    private UserDto getUserDto(Long id) {
        return userRowMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_MESSAGE)));
    }

    private ItemDto getItemDto(Long id) {
        return toItemDto(itemRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.ITEM_NOT_FOUND_MESSAGE)));
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException(ErrorMessage.BOOKING_NOT_FOUND_MESSAGE));
    }

    private Booking toBooking(BookingDto bookingDto) {
        User booker = userRowMapper.toUser(bookingDto.getBookerDto());
        Item item = toItem(bookingDto.getItemDto());
        return bookingRowMapper.toBooking(bookingDto, booker, item);
    }

    private BookingDto toBookingDto(Booking booking) {
        UserDto bookerDto = userRowMapper.toUserDto(booking.getBooker());
        ItemDto itemDto = toItemDto(booking.getItem());
        return bookingRowMapper.toBookingDto(booking, bookerDto, itemDto);
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
}
