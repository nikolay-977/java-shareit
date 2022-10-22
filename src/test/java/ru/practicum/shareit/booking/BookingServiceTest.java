package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingState.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private EntityManager entityManager;
    private User userOne;
    private User userTwo;
    private Item itemOne;
    private Item itemTwo;
    private Booking bookingOne;
    private Booking bookingTwo;

    @BeforeEach
    void setUp() {
        userOne = User.builder()
                .name("Name of user one")
                .email("userone@mail.com")
                .build();
        entityManager.persist(userOne);

        userTwo = User.builder()
                .name("Name of user two")
                .email("usertwo@mail.com")
                .build();

        itemOne = Item.builder()
                .available(true)
                .name("Name of item one")
                .description("Description of item one")
                .owner(userOne)
                .build();
        itemOne.setAvailable(true);


        itemTwo = Item.builder()
                .available(true)
                .name("Name of item two")
                .description("Description of item two")
                .owner(userOne)
                .build();

        bookingOne = Booking.builder()
                .item(itemOne)
                .booker(userOne)
                .status(APPROVED)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        bookingTwo = Booking.builder()
                .item(itemTwo)
                .booker(userTwo)
                .build();
    }

    @Test
    void createAccessDenied() {
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);
        BookingDto bookingDto = BookingRowMapper.toBookingDto(bookingOne);

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(userOne.getId(), bookingDto)
        );

        assertEquals("Access denied", notFoundException.getMessage());
    }

    @Test
    void createDateNotValid() {
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        itemOne.setOwner(userTwo);
        bookingOne.setStart(LocalDateTime.now().minusDays(1));
        bookingOne.setEnd(LocalDateTime.now().minusDays(5));
        BookingDto bookingDto = BookingRowMapper.toBookingDto(bookingOne);

        BadRequestException badRequestException = assertThrows(
                BadRequestException.class,
                () -> bookingService.create(userOne.getId(), bookingDto)
        );

        assertEquals("The start date should be earlier than the end date", badRequestException.getMessage());
    }

    @Test
    void createItemNotAvailable() {
        entityManager.persist(itemOne);
        itemOne.setOwner(userTwo);
        itemOne.setAvailable(false);
        BookingDto bookingDto = BookingRowMapper.toBookingDto(bookingOne);

        BadRequestException badRequestException = assertThrows(
                BadRequestException.class,
                () -> bookingService.create(userOne.getId(), bookingDto)
        );

        assertEquals("The item not available", badRequestException.getMessage());
    }

    @Test
    void createBookingSuccess() {
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        itemOne.setOwner(userTwo);
        BookingDto bookingDto = BookingRowMapper.toBookingDto(bookingOne);
        bookingService.create(userOne.getId(), bookingDto);
    }

    @Test
    void updateStatusAccessDenied() {
        entityManager.persist(userTwo);
        itemOne.setOwner(userTwo);
        entityManager.persist(itemOne);
        bookingOne.setItem(itemOne);
        entityManager.persist(bookingOne);

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateStatus(userOne.getId(), bookingOne.getId(), true)
        );

        assertEquals("Access denied", notFoundException.getMessage());
    }

    @Test
    void updateStatusIsAlreadyApproved() {
        entityManager.persist(itemOne);
        bookingOne.setItem(itemOne);
        bookingOne.setStatus(APPROVED);
        entityManager.persist(bookingOne);

        BadRequestException badRequestException = assertThrows(
                BadRequestException.class,
                () -> bookingService.updateStatus(userOne.getId(), bookingOne.getId(), true)
        );

        assertEquals("The status 'APPROVED' is already set", badRequestException.getMessage());
    }

    @Test
    void updateStatusApprovedSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setItem(itemOne);
        bookingOne.setStatus(WAITING);
        entityManager.persist(bookingOne);

        assertEquals(APPROVED, bookingService.updateStatus(userOne.getId(), bookingOne.getId(), true).getStatus());
    }

    @Test
    void updateStatusRejectedSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setItem(itemOne);
        bookingOne.setStatus(WAITING);
        entityManager.persist(bookingOne);

        assertEquals(REJECTED, bookingService.updateStatus(userOne.getId(), bookingOne.getId(), false).getStatus());
    }


    @Test
    void getByIdOtherUserAccessDenied() {
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        entityManager.persist(bookingOne);
        bookingOne.setItem(itemOne);

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(userTwo.getId(), bookingOne.getId())
        );

        assertEquals("Access denied", notFoundException.getMessage());
    }

    @Test
    void getByIdOwnerSuccess() {
        entityManager.persist(userTwo);
        itemOne.setOwner(userTwo);
        bookingOne.setItem(itemOne);
        entityManager.persist(itemOne);
        entityManager.persist(bookingOne);

        assertEquals(bookingOne.getId(), bookingService.getById(userTwo.getId(), bookingOne.getId()).getId());
    }

    @Test
    void getByIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setItem(itemOne);
        entityManager.persist(bookingOne);
        assertEquals(bookingOne.getId(), bookingService.getById(userOne.getId(), bookingOne.getId()).getId());
    }

    @Test
    void getAllByOwnerIdSuccess() {
        entityManager.persist(itemOne);
        bookingTwo.setStatus(APPROVED);
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userOne.getId(), "ALL", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllApprovedOwnerIdSuccess() {
        entityManager.persist(itemOne);
        bookingTwo.setStatus(APPROVED);
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userOne.getId(), "APPROVED", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllWaitingByOwnerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(WAITING);
        bookingTwo.setStatus(WAITING);
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userOne.getId(), "WAITING", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllRejectedByOwnerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(REJECTED);
        bookingTwo.setStatus(REJECTED);
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userOne.getId(), "REJECTED", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllFutureByOwnerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(FUTURE);
        bookingTwo.setStatus(APPROVED);
        bookingOne.setStart(LocalDateTime.now().plusDays(2));
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoOne, bookingDtoTwo);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userOne.getId(), "FUTURE", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllPastByOwnerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(APPROVED);
        bookingTwo.setStatus(APPROVED);
        bookingOne.setEnd(LocalDateTime.now().minusDays(5));
        bookingTwo.setEnd(LocalDateTime.now().minusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoOne, bookingDtoTwo);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userOne.getId(), "PAST", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllCurrentByOwnerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(APPROVED);
        bookingTwo.setStatus(APPROVED);
        bookingOne.setStart(LocalDateTime.now().minusDays(12));
        bookingOne.setEnd(LocalDateTime.now().plusDays(5));
        bookingTwo.setStart(LocalDateTime.now().minusDays(11));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByOwnerId(userOne.getId(), "CURRENT", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllUnknownStateByOwnerIdSuccess() {
        BadRequestException badRequestException = assertThrows(
                BadRequestException.class,
                () -> bookingService.getAllByOwnerId(userOne.getId(), "UNKNOWN_STATE", 0, 10)
        );

        assertEquals("Unknown state: UNKNOWN_STATE", badRequestException.getMessage());
    }

    @Test
    void getAllByBookerIdSuccess() {
        entityManager.persist(itemOne);
        bookingTwo.setStatus(APPROVED);
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByBookerId(userOne.getId(), "ALL", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllApprovedByBookerIdSuccess() {
        entityManager.persist(itemOne);
        bookingTwo.setStatus(APPROVED);
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByBookerId(userOne.getId(), "APPROVED", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllWaitingByBookerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(WAITING);
        bookingTwo.setStatus(WAITING);
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByBookerId(userOne.getId(), "WAITING", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllRejectedByBookerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(REJECTED);
        bookingTwo.setStatus(REJECTED);
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoTwo, bookingDtoOne);

        List<BookingDto> actualList = bookingService.getAllByBookerId(userOne.getId(), "REJECTED", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllFutureByBookerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(FUTURE);
        bookingTwo.setStatus(APPROVED);
        bookingOne.setStart(LocalDateTime.now().plusDays(2));
        bookingTwo.setStart(LocalDateTime.now().plusDays(1));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoOne, bookingDtoTwo);

        List<BookingDto> actualList = bookingService.getAllByBookerId(userOne.getId(), "FUTURE", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllPastByBookerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(APPROVED);
        bookingTwo.setStatus(APPROVED);
        bookingOne.setEnd(LocalDateTime.now().minusDays(5));
        bookingTwo.setEnd(LocalDateTime.now().minusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoOne, bookingDtoTwo);

        List<BookingDto> actualList = bookingService.getAllByBookerId(userOne.getId(), "PAST", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllCurrentByBookerIdSuccess() {
        entityManager.persist(itemOne);
        bookingOne.setStatus(APPROVED);
        bookingTwo.setStatus(APPROVED);
        bookingOne.setStart(LocalDateTime.now().minusDays(12));
        bookingOne.setEnd(LocalDateTime.now().plusDays(5));
        bookingTwo.setStart(LocalDateTime.now().minusDays(11));
        bookingTwo.setEnd(LocalDateTime.now().plusDays(10));
        bookingTwo.setBooker(userOne);
        entityManager.persist(itemTwo);
        entityManager.persist(bookingOne);
        entityManager.persist(bookingTwo);
        BookingDto bookingDtoOne = BookingRowMapper.toBookingDto(bookingOne);
        BookingDto bookingDtoTwo = BookingRowMapper.toBookingDto(bookingTwo);
        List<BookingDto> expectedList = List.of(bookingDtoOne, bookingDtoTwo);

        List<BookingDto> actualList = bookingService.getAllByBookerId(userOne.getId(), "CURRENT", 0, 10);

        assertIterableEquals(expectedList, actualList);
    }

    @Test
    void getAllUnknownStateByBookerIdSuccess() {
        BadRequestException badRequestException = assertThrows(
                BadRequestException.class,
                () -> bookingService.getAllByBookerId(userOne.getId(), "UNKNOWN_STATE", 0, 10)
        );

        assertEquals("Unknown state: UNKNOWN_STATE", badRequestException.getMessage());
    }
}
