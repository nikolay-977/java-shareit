package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exception.NotFoundException;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.server.exception.ErrorMessage.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserRowMapper userRowMapper;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userRowMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        return userRowMapper.toUserDto(userRepository.save(userRowMapper.toUser(userDto)));
    }

    @Override
    public UserDto getById(Long userId) {
        return userRowMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE)));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        UserDto userDtoToUpdate = getById(userId);
        userDtoToUpdate.setName(userDto.getName() != null ? userDto.getName() : userDtoToUpdate.getName());
        userDtoToUpdate.setEmail(userDto.getEmail() != null ? userDto.getEmail() : userDtoToUpdate.getEmail());

        return userRowMapper.toUserDto(userRepository.save(userRowMapper.toUser(userDtoToUpdate)));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
