package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingState.APPROVED;
import static ru.practicum.shareit.exception.ErrorMessage.REQUEST_NOT_FOUND_MESSAGE;
import static ru.practicum.shareit.exception.ErrorMessage.USER_NOT_FOUND_MESSAGE;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private EntityManager entityManager;
    private User userOne;
    private User userTwo;
    private UserDto userDtoOne;
    private UserDto userDtoTwo;
    private Item itemOne;
    private Item itemTwo;
    private ItemDto itemDtoOne;
    private ItemDto itemDtoTwo;

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

        userDtoOne = UserDto.builder()
                .name("Name of user one")
                .email("userone@mail.com")
                .build();

        userDtoTwo = UserDto.builder()
                .name("Name of user two")
                .email("usertwo@mail.com")
                .build();

        itemOne = Item.builder()
                .name("Name of item one")
                .description("Description of item one")
                .available(true)
                .build();

        itemTwo = new Item();

        itemDtoOne = ItemDto.builder()
                .name("Name of item one")
                .description("Description of item one")
                .available(true)
                .build();

        itemDtoTwo = new ItemDto();
    }

    @Test
    void createItemUserNotFoundTest() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> itemService.create(2L, itemDtoOne));
        assertEquals(USER_NOT_FOUND_MESSAGE, notFoundException.getMessage());
    }

    @Test
    void createItemRequestNotFoundTest() {
        itemDtoOne.setRequestId(2L);
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> itemService.create(userOne.getId(), itemDtoOne));
        assertEquals(REQUEST_NOT_FOUND_MESSAGE, notFoundException.getMessage());
    }

    @Test
    void createItemTest() {
        ItemDto savedItem = itemService.create(userOne.getId(), itemDtoOne);
        itemDtoOne.setId(savedItem.getId());
        itemDtoOne.setOwnerDto(savedItem.getOwnerDto());
        assertEquals(itemDtoOne, savedItem);
    }

    @Test
    void updateItemOwnerTest() {
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);
        userDtoTwo.setId(userTwo.getId());
        itemDtoTwo.setOwnerDto(userDtoTwo);

        ItemDto actualItemDto = itemService.update(userOne.getId(), itemDtoTwo, itemOne.getId());

        assertEquals(userTwo.getId(), actualItemDto.getOwnerDto().getId());
    }

    @Test
    void updateItemAvailableTest() {
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);
        itemDtoTwo.setAvailable(false);

        ItemDto actualItemDto = itemService.update(userOne.getId(), itemDtoTwo, itemOne.getId());

        assertFalse(actualItemDto.getAvailable());
    }

    @Test
    void updateItemDescriptionTest() {
        itemDtoTwo.setDescription("Update description");
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);

        ItemDto actualItemDto = itemService.update(userOne.getId(), itemDtoTwo, itemOne.getId());

        assertEquals("Update description", actualItemDto.getDescription());
    }

    @Test
    void updateItemNameTest() {
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);
        itemDtoTwo.setName("Update name");

        ItemDto actualItemDto = itemService.update(userOne.getId(), itemDtoTwo, itemOne.getId());

        assertEquals("Update name", actualItemDto.getName());
    }

    @Test
    void updateWithOtherUserTest() {
        entityManager.persist(itemOne);
        entityManager.persist(userTwo);
        itemOne.setOwner(userOne);
        itemDtoTwo.setName("Update name");

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> itemService.update(userTwo.getId(), itemDtoTwo, itemOne.getId()));

        assertEquals("Update with other user", notFoundException.getMessage());
    }

    @Test
    void getByIdItemTest() {
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);

        ItemInfoDto actualItemDto = itemService.getById(userOne.getId(), itemOne.getId());

        assertEquals(itemOne.getId(), actualItemDto.getId());
    }

    @Test
    void getAllItemsTest() {
        itemTwo.setName("Name of item two");
        itemTwo.setDescription("Description of item two");
        entityManager.persist(itemOne);
        entityManager.persist(itemTwo);
        itemOne.setOwner(userOne);
        itemTwo.setOwner(userOne);

        List<ItemInfoDto> actualItemDtoList = itemService.getAll(userOne.getId(), 0, 10);

        assertEquals(2, actualItemDtoList.size());
    }

    @Test
    void addCommentTest() {
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);
        Booking booking = new Booking();
        booking.setBooker(userOne);
        booking.setItem(itemOne);
        booking.setStatus(APPROVED);
        booking.setEnd(LocalDateTime.now().minusDays(1));
        entityManager.persist(booking);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment of item");

        CommentDto actualCommentDto = itemService.addComment(userOne.getId(), itemOne.getId(), commentDto);

        assertEquals("Comment of item", actualCommentDto.getText());
    }

    @Test
    void searchItemTest() {
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);

        List<ItemDto> actualItemDtoList = itemService.search(userOne.getId(), "descr");

        assertTrue(actualItemDtoList.get(0).getDescription().toLowerCase().contains("descr"));
    }

    @Test
    void searchByNameItemTest() {
        entityManager.persist(itemOne);
        itemOne.setOwner(userOne);

        List<ItemDto> actualItemDtoList = itemService.search(userOne.getId(), "name");

        assertTrue(actualItemDtoList.get(0).getName().toLowerCase().contains("name"));
    }
}
