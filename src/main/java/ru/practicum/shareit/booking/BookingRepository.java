package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatusIsOrderByStartDesc(Long userId, BookingState status, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStatusIsOrderByStartDesc(Long userId, BookingState status, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findTop2ByItem_Owner_IdAndItem_IdOrderByStartAsc(Long userId, Long itemId);

    Optional<Booking> findFirstByBooker_IdAndItem_IdAndEndBefore(Long userId, Long itemId, LocalDateTime now);
}
