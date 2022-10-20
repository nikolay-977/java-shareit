package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorMessage.USER_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserRowMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return UserRowMapper.toUserDto(userRepository.save(UserRowMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto getById(Long userId) {
        return UserRowMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE)));
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long userId) {
        UserDto userDtoToUpdate = UserRowMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого пользователя")));

        userDtoToUpdate.setName(userDto.getName() != null ? userDto.getName() : userDtoToUpdate.getName());
        userDtoToUpdate.setEmail(userDto.getEmail() != null ? userDto.getEmail() : userDtoToUpdate.getEmail());

        return UserRowMapper.toUserDto(userRepository.save(UserRowMapper.toUser(userDtoToUpdate)));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
