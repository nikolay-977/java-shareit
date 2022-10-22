package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserRowMapper;
import ru.practicum.shareit.utils.ShareItPageRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ErrorMessage.REQUEST_NOT_FOUND_MESSAGE;
import static ru.practicum.shareit.exception.ErrorMessage.USER_NOT_FOUND_MESSAGE;

@Transactional
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        checkIsUserExist(userId);
        itemRequestDto.setOwnerId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        return ItemRequestRowMapper.toItemRequestDto(itemRequestRepository.save(ItemRequestRowMapper.toItemRequest(itemRequestDto)));
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        checkIsUserExist(userId);
        return itemRequestRepository.findAllByOwnerIdOrderByCreated(userId).stream().map(ItemRequestRowMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllWithPagination(Integer from, Integer size, Long userId) {
        checkIsUserExist(userId);
        Pageable pageable = ShareItPageRequest.of(from, size);
        return itemRequestRepository.findAllByIdIsNotOrderByCreated(userId, pageable).stream().map(ItemRequestRowMapper::toItemRequestDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        checkIsUserExist(userId);
        return ItemRequestRowMapper.toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(REQUEST_NOT_FOUND_MESSAGE)));
    }

    private void checkIsUserExist(Long id) {
        UserRowMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE)));
    }
}
