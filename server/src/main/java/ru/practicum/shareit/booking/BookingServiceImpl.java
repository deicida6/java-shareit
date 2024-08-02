package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AvailableItemException;
import ru.practicum.shareit.exception.InvalidStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingRequestDto addBooking(BookingCreateDto bookingCreateDto, Long userId) {
        if (bookingCreateDto.getEnd() == null || bookingCreateDto.getStart() == null) {
            throw new ValidationException("Начало или конец аренды должен быть заполнен");
        }
        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart()) || bookingCreateDto.getEnd().isEqual(bookingCreateDto.getStart())) {
            throw new ValidationException("Конец букинга не может равняться или быть раньше начала");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Нет юзера с таким ID = " + userId));
        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Нет вещи с таким ID = " + bookingCreateDto.getItemId()));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не может арендовать вещь, он уже ее владелец");
        }
        if (!item.getAvailable()) {
            throw new AvailableItemException("Вещь с id " + bookingCreateDto.getItemId() + " не доступна");
        }
        Booking booking = BookingMapper.toBooking(bookingCreateDto,user,item);
        return BookingMapper.toBookingRequestDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingRequestDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved) {
        userRepository.findById(userId).orElseThrow(() ->
                new ValidationException("Нет юзера с таким ID = " + userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Не найдено бронирование с id = " + bookingId));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Только владелец может апрувить");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new AvailableItemException("Нельзя апрувнуть или отклонить, статус не Waiting");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingRequestDto(bookingRepository.save(booking));
    }

    @Override
    public BookingRequestDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование на найден с таким id = " + bookingId));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Только букер, или владелец может смотреть эту вещь с id " + booking.getItem().getId());
        }
        return BookingMapper.toBookingRequestDto(booking);
    }

    @Override
    public Collection<BookingRequestDto> getAllBookingsByUser(Long userId, String state) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Unknown state: " + state);
        }
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя нет с таким id = " + userId));
        Collection<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };
        return BookingMapper.toListBookingRequestDto(bookings);
    }

    @Override
    public Collection<BookingRequestDto> getAllBookingsAllItemsByOwner(Long userId, String state) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Unknown state: " + state);
        }
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователя нет с таким id = " + userId));
        Collection<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
        };
        return BookingMapper.toListBookingRequestDto(bookings);
    }
}
