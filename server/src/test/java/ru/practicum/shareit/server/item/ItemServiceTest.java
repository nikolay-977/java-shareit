package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.comment.dto.CommentDto;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.ItemInfoDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.server.booking.BookingState.APPROVED;
import static ru.practicum.shareit.server.exception.ErrorMessage.REQUEST_NOT_FOUND_MESSAGE;
import static ru.practicum.shareit.server.exception.ErrorMessage.USER_NOT_FOUND_MESSAGE;

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
        itemOne.setOwner(userOne);
        entityManager.persist(userTwo);
        entityManager.persist(itemOne);
        userDtoTwo.setId(userTwo.getId());
        itemDtoTwo.setOwnerDto(userDtoTwo);

        ItemDto actualItemDto = itemService.update(userOne.getId(), itemDtoTwo, itemOne.getId());

        assertEquals(userTwo.getId(), actualItemDto.getOwnerDto().getId());
    }

    @Test
    void updateItemAvailableTest() {
        itemOne.setOwner(userOne);
        entityManager.persist(itemOne);
        itemDtoTwo.setAvailable(false);

        ItemDto actualItemDto = itemService.update(userOne.getId(), itemDtoTwo, itemOne.getId());

        assertFalse(actualItemDto.getAvailable());
    }

    @Test
    void updateItemDescriptionTest() {
        itemOne.setOwner(userOne);
        itemDtoTwo.setDescription("Update description");
        entityManager.persist(itemOne);

        ItemDto actualItemDto = itemService.update(userOne.getId(), itemDtoTwo, itemOne.getId());

        assertEquals("Update description", actualItemDto.getDescription());
    }

    @Test
    void updateItemNameTest() {
        itemOne.setOwner(userOne);
        entityManager.persist(itemOne);
        itemDtoTwo.setName("Update name");

        ItemDto actualItemDto = itemService.update(userOne.getId(), itemDtoTwo, itemOne.getId());

        assertEquals("Update name", actualItemDto.getName());
    }

    @Test
    void updateWithOtherUserTest() {
        itemOne.setOwner(userOne);
        entityManager.persist(itemOne);
        entityManager.persist(userTwo);
        itemDtoTwo.setName("Update name");

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> itemService.update(userTwo.getId(), itemDtoTwo, itemOne.getId()));

        assertEquals("Update with other user", notFoundException.getMessage());
    }

    @Test
    void getByIdItemTest() {
        itemOne.setOwner(userOne);
        entityManager.persist(itemOne);

        ItemInfoDto actualItemDto = itemService.getById(userOne.getId(), itemOne.getId());

        assertEquals(itemOne.getId(), actualItemDto.getId());
    }

    @Test
    void getAllItemsTest() {
        itemOne.setOwner(userOne);
        itemTwo.setOwner(userOne);
        itemTwo.setName("Name of item two");
        itemTwo.setDescription("Description of item two");
        itemTwo.setAvailable(true);
        entityManager.persist(itemOne);
        entityManager.persist(itemTwo);

        List<ItemInfoDto> actualItemDtoList = itemService.getAll(userOne.getId(), 0, 10);

        assertEquals(2, actualItemDtoList.size());
    }

    @Test
    void addCommentTest() {
        itemOne.setOwner(userOne);
        entityManager.persist(itemOne);
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
        itemOne.setOwner(userOne);
        entityManager.persist(itemOne);

        List<ItemDto> actualItemDtoList = itemService.search(userOne.getId(), "descr");

        assertTrue(actualItemDtoList.get(0).getDescription().toLowerCase().contains("descr"));
    }

    @Test
    void searchByNameItemTest() {
        itemOne.setOwner(userOne);
        entityManager.persist(itemOne);

        List<ItemDto> actualItemDtoList = itemService.search(userOne.getId(), "name");

        assertTrue(actualItemDtoList.get(0).getName().toLowerCase().contains("name"));
    }
}