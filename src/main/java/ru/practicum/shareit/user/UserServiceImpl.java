package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll();
    }

    @Override
    public UserDto create(User user) {
        return userRepository.create(user);
    }

    @Override
    public UserDto getById(long userId) {
        return userRepository.getById(userId);
    }

    @Override
    public UserDto update(User user, long userId) {
        return userRepository.update(user, userId);
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }
}
