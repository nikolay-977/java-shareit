package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemRowMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestRowMapper;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private EntityManager entityManager;
    private User user;
    private Item itemOne;
    private Item itemTwo;
    private ItemDto itemDtoOne;
    private ItemRequest itemRequestOne;
    private ItemRequest itemRequestTwo;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Name of user")
                .email("user@mail.ru")
                .build();

        itemOne = Item.builder()
                .name("Name of item one")
                .description("Description of item one")
                .build();

        itemTwo = Item.builder()
                .name("Name of item two")
                .description("Description of item two")
                .build();

        itemRequestOne = ItemRequest.builder()
                .description("Description of item request one")
                .build();

        itemRequestTwo = ItemRequest.builder()
                .description("Description of item request two")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description("Description of item request")
                .build();
    }

    @Test
    void createItemRequestTest() {
        itemOne.setOwner(user);
        entityManager.persist(user);
        entityManager.persist(itemOne);
        itemDtoOne = ItemRowMapper.toItemDto(itemOne);
        itemRequestDto.setItemDtoList(List.of(itemDtoOne));

        ItemRequestDto actualRequestDto = itemRequestService.create(user.getId(), itemRequestDto);

        assertEquals(itemRequestDto.getDescription(), actualRequestDto.getDescription());
        assertEquals(itemRequestDto.getOwnerId(), actualRequestDto.getOwnerId());
    }

    @Test
    void getAllByUserIdTest() {
        entityManager.persist(user);
        entityManager.persist(itemOne);
        entityManager.persist(itemTwo);
        itemOne.setOwner(user);
        itemTwo.setOwner(user);
        itemRequestOne.setOwnerId(user.getId());
        itemRequestOne.setItemList(List.of(itemOne));
        itemRequestTwo.setOwnerId(user.getId());
        itemRequestTwo.setItemList(List.of(itemOne));
        entityManager.persist(itemRequestOne);
        entityManager.persist(itemRequestTwo);

        List<ItemRequestDto> actualRequestDtoList = itemRequestService.getAllByUserId(user.getId());

        assertEquals(2, actualRequestDtoList.size());
    }

    @Test
    void getAllByUserIdWithPaginationTest() {
        entityManager.persist(user);
        entityManager.persist(itemOne);
        entityManager.persist(itemTwo);
        itemOne.setOwner(user);
        itemTwo.setOwner(user);
        itemRequestOne.setOwnerId(user.getId());
        itemRequestOne.setItemList(List.of(itemOne));
        itemRequestTwo.setOwnerId(user.getId());
        itemRequestTwo.setItemList(List.of(itemOne));
        entityManager.persist(itemRequestOne);
        entityManager.persist(itemRequestTwo);

        List<ItemRequestDto> actualRequestDtoList = itemRequestService.getAllWithPagination(0, 10, user.getId());

        assertEquals(2, actualRequestDtoList.size());
    }

    @Test
    void getItemRequestByIdTest() {
        entityManager.persist(user);
        entityManager.persist(itemOne);
        itemOne.setOwner(user);
        itemRequestOne.setOwnerId(user.getId());
        itemRequestOne.setItemList(List.of(itemOne));
        entityManager.persist(itemRequestOne);
        itemRequestDto = ItemRequestRowMapper.toItemRequestDto(itemRequestOne);

        ItemRequestDto actualItemRequest = itemRequestService.getItemRequestById(user.getId(), itemRequestOne.getId());

        assertEquals(itemRequestDto.getId(), actualItemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), actualItemRequest.getDescription());
    }
}
