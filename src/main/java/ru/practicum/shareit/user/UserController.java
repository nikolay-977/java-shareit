package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto user) {
        return userService.create(user);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
