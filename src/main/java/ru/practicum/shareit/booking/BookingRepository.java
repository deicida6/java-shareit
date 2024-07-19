package ru.practicum.shareit.booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    Collection<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long userId, LocalDateTime start, LocalDateTime end);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status);

    Optional<BookingItemDto> findFirstByItemIdAndStatusAndStartIsAfterOrderByStart(Long itemId, Status status, LocalDateTime now);

    Optional<BookingItemDto> findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(Long itemId, Status status, LocalDateTime now);

    Optional<BookingItemDto> findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(Long userId, Long itemId, Status status, LocalDateTime now);
}