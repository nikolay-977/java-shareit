package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserRowMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserRowMapper.toUserDto(userRepository.create(UserRowMapper.toUser(userDto)));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserRowMapper.toUserDto(userRepository.getById(userId));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        return UserRowMapper.toUserDto(userRepository.update(UserRowMapper.toUser(userDto), userId));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }
}
