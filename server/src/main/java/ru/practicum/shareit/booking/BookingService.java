package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Collection;

public interface BookingService {
    BookingRequestDto addBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingRequestDto approveOrRejectBooking(Long userId, Long bookingId, Boolean approved);

    BookingRequestDto getById(Long bookingId, Long userId);

    Collection<BookingRequestDto> getAllBookingsByUser(Long userId, String state);

    Collection<BookingRequestDto> getAllBookingsAllItemsByOwner(Long userId, String state);
}