package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemRowMapper;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserRowMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingState.*;
import static ru.practicum.shareit.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        final long finalUserId = userId;

        UserDto bookerDto = UserRowMapper.toUserDto(userRepository.findById(finalUserId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE)));
        ItemDto itemDto = ItemRowMapper.toItemDto(itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_MESSAGE)));

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
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(BOOKING_NOT_FOUND_MESSAGE));

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
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND_MESSAGE));

        boolean isUserIdOwner = booking.getItem().getOwner().getId().equals(userId);
        boolean isUserIdBooker = booking.getBooker().getId().equals(userId);

        if (!(isUserIdOwner || isUserIdBooker)) {
            throw new NotFoundException(ACCESS_DENIED_MESSAGE);
        }

        return BookingRowMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBookerId(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));

        BookingState bookingState = getState(state);

        List<Booking> bookingList;

        switch (bookingState) {
            case ALL: {
                bookingList = bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
                break;
            }
            case APPROVED:
            case WAITING:
            case REJECTED: {
                bookingList = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(userId, bookingState);
                break;
            }
            case FUTURE: {
                bookingList = bookingRepository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case PAST: {
                bookingList = bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case CURRENT: {
                bookingList = bookingRepository.findAllByBooker_IdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            default: {
                throw new NotFoundException(NO_HANDLER_STATUS);
            }
        }

        return bookingList.stream().map(BookingRowMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwnerId(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));

        BookingState bookingState = getState(state);

        List<Booking> bookingList;

        switch (bookingState) {
            case ALL: {
                bookingList = bookingRepository.findAllByItem_Owner_IdOrderByStartDesc(userId);
                break;
            }
            case APPROVED:
            case WAITING:
            case REJECTED: {
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(userId, bookingState);
                break;
            }
            case FUTURE: {
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case PAST: {
                bookingList = bookingRepository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            }
            case CURRENT: {
                bookingList = bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            default: {
                throw new NotFoundException(NO_HANDLER_STATUS);
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
}
