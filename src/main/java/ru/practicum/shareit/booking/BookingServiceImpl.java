package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemRowMapper;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserRowMapper;
import ru.practicum.shareit.utils.ShareItPageRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingState.*;
import static ru.practicum.shareit.exception.ErrorMessage.*;

@Transactional
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        final long finalUserId = userId;

        UserDto bookerDto = getUserDto(finalUserId);
        ItemDto itemDto = getItemDto(bookingDto.getItemId());

        if (!itemDto.getAvailable()) {
            throw new BadRequestException(ITEM_NOT_AVAILABLE);
        }
        if (itemDto.getOwnerDto().getId() == finalUserId) {
            throw new NotFoundException(ACCESS_DENIED_MESSAGE);
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new BadRequestException(START_DATE_SHOULD_BE_EARLIER);
        }

        bookingDto.setBookerDto(bookerDto);
        bookingDto.setItemDto(itemDto);
        bookingDto.setStatus(WAITING);

        return BookingRowMapper.toBookingDto(bookingRepository.save(BookingRowMapper.toBooking(bookingDto)));
    }

    @Override
    public BookingDto updateStatus(Long userId, Long bookingId, Boolean status) {
        checkIsUserExist(userId);
        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(ACCESS_DENIED_MESSAGE);
        }

        BookingState newStatus = status ? APPROVED : REJECTED;

        if (booking.getStatus() == newStatus) {
            throw new BadRequestException(IS_APPROVED_ALREADY_SET);
        }

        booking.setStatus(newStatus);

        return BookingRowMapper.toBookingDto(bookingRepository.save(booking));
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

        return BookingRowMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBookerId(Long userId, String state, Integer from, Integer size) {
        checkIsUserExist(userId);
        Pageable pageable = ShareItPageRequest.of(from, size);
        BookingState bookingState = getState(state);
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL: {
                bookingList = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId, pageable);
                break;
            }
            case APPROVED:
            case WAITING:
            case REJECTED: {
                bookingList = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(userId, bookingState, pageable);
                break;
            }
            case FUTURE: {
                bookingList = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case PAST: {
                bookingList = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case CURRENT: {
                bookingList = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            }
        }

        return bookingList.stream().map(BookingRowMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwnerId(Long userId, String state, Integer from, Integer size) {
        checkIsUserExist(userId);
        Pageable pageable = ShareItPageRequest.of(from, size);
        BookingState bookingState = getState(state);
        List<Booking> bookingList = new ArrayList<>();

        switch (bookingState) {
            case ALL: {
                bookingList = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId, pageable);
                break;
            }
            case APPROVED:
            case WAITING:
            case REJECTED: {
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(userId, bookingState, pageable);
                break;
            }
            case FUTURE: {
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case PAST: {
                bookingList = bookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageable);
                break;
            }
            case CURRENT: {
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            }
        }

        return bookingList.stream().map(BookingRowMapper::toBookingDto).collect(Collectors.toList());
    }

    private BookingState getState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format(UNKNOWN_STATE, state));
        }
    }

    private void checkIsUserExist(Long id) {
        getUserDto(id);
    }

    private UserDto getUserDto(Long id) {
        return UserRowMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE)));
    }

    private ItemDto getItemDto(Long id) {
        return ItemRowMapper.toItemDto(itemRepository.findById(id).orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_MESSAGE)));
    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND_MESSAGE));
    }
}
