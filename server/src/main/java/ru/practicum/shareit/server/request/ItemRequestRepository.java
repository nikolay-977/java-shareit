package ru.practicum.shareit.server.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByOwnerIdOrderByCreated(Long userId);

    List<ItemRequest> findAllByIdIsNotOrderByCreated(Long userId, Pageable pageable);

    Optional<ItemRequest> findById(Long requestId);
}
