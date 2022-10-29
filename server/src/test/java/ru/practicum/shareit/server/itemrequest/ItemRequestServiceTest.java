package ru.practicum.shareit.server.itemrequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRowMapper;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.ItemRequest;
import ru.practicum.shareit.server.request.ItemRequestRowMapper;
import ru.practicum.shareit.server.request.ItemRequestService;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRowMapper;
import ru.practicum.shareit.server.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRowMapper userRowMapper;
    @Autowired
    private ItemRowMapper itemRowMapper;
    @Autowired
    private ItemRequestRowMapper itemRequestRowMapper;
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
                .available(true)
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
        itemDtoOne = toItemDto(itemOne);
        itemRequestDto.setItemDtoList(List.of(itemDtoOne));

        ItemRequestDto actualRequestDto = itemRequestService.create(user.getId(), itemRequestDto);

        assertEquals(itemRequestDto.getDescription(), actualRequestDto.getDescription());
        assertEquals(itemRequestDto.getOwnerId(), actualRequestDto.getOwnerId());
    }

    @Test
    void getAllByUserIdTest() {
        entityManager.persist(user);
        itemOne.setOwner(user);
        itemTwo.setOwner(user);
        itemTwo.setAvailable(true);
        entityManager.persist(itemOne);
        entityManager.persist(itemTwo);
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
        itemOne.setOwner(user);
        itemTwo.setOwner(user);
        itemTwo.setAvailable(true);
        entityManager.persist(itemOne);
        entityManager.persist(itemTwo);
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
        itemOne.setOwner(user);
        entityManager.persist(itemOne);
        itemRequestOne.setOwnerId(user.getId());
        itemRequestOne.setItemList(List.of(itemOne));
        entityManager.persist(itemRequestOne);
        itemRequestDto = toItemRequestDto(itemRequestOne);

        ItemRequestDto actualItemRequest = itemRequestService.getItemRequestById(user.getId(), itemRequestOne.getId());

        assertEquals(itemRequestDto.getId(), actualItemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), actualItemRequest.getDescription());
    }

    private ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> itemDtoList = itemRequest.getItemList().stream().map(this::toItemDto).collect(Collectors.toList());
        return itemRequestRowMapper.toItemRequestDto(itemRequest, itemDtoList);
    }

    private ItemDto toItemDto(Item item) {
        UserDto ownerDto = userRowMapper.toUserDto(item.getOwner());
        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }
        return itemRowMapper.toItemDto(item, ownerDto, requestId);
    }
}
