package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.exception.ErrorMessage.USER_NOT_FOUND_MESSAGE;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private EntityManager entityManager;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Name of user")
                .email("user@mail.ru")
                .build();

        userDto = UserDto.builder()
                .name("Name of user")
                .email("user@mail.ru")
                .build();
    }

    @Test
    void createUserTest() {
        UserDto actualUserDto = userService.create(userDto);
        assertEquals(userDto.getName(), actualUserDto.getName());
        assertEquals(userDto.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void getUserByIdTest() {
        entityManager.persist(user);
        UserDto actualUserDto = userService.getById(user.getId());
        assertEquals(user.getId(), actualUserDto.getId());
        assertEquals(user.getName(), actualUserDto.getName());
        assertEquals(user.getEmail(), actualUserDto.getEmail());
    }

    @Test
    void getUserByIdUserNotFoundTest() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> userService.getById(99L));
        assertEquals(USER_NOT_FOUND_MESSAGE, notFoundException.getMessage());
    }

    @Test
    void getAll() {
        entityManager.persist(user);
        List<UserDto> actualUserDtoList = userService.getAll();
        assertEquals(user.getId(), actualUserDtoList.get(0).getId());
        assertEquals(user.getName(), actualUserDtoList.get(0).getName());
        assertEquals(user.getEmail(), actualUserDtoList.get(0).getEmail());
    }

    @Test
    void updateUserByIdTest() {
        entityManager.persist(user);
        userDto.setName("Update name");
        UserDto actualUserDto = userService.update(userDto, user.getId());
        assertEquals(user.getId(), actualUserDto.getId());
        assertEquals("Update name", actualUserDto.getName());
    }

    @Test
    void deleteByIdTest() {
        entityManager.persist(user);
        userService.delete(user.getId());
        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> userService.getById(user.getId()));
        assertEquals(USER_NOT_FOUND_MESSAGE, notFoundException.getMessage());
    }
}
