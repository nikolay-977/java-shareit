package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorMessage.USER_NOT_FOUND_MESSAGE;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserRowMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserRowMapper.toUserDto(userRepository.save(UserRowMapper.toUser(userDto)));
    }

    @Override
    public UserDto getById(Long userId) {
        return UserRowMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE)));
    }

    @Override
    public UserDto update(UserDto userDto, Long userId) {
        UserDto userDtoToUpdate = getById(userId);
        userDtoToUpdate.setName(userDto.getName() != null ? userDto.getName() : userDtoToUpdate.getName());
        userDtoToUpdate.setEmail(userDto.getEmail() != null ? userDto.getEmail() : userDtoToUpdate.getEmail());

        return UserRowMapper.toUserDto(userRepository.save(UserRowMapper.toUser(userDtoToUpdate)));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
