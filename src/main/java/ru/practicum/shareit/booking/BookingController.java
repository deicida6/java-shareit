package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingRequestDto addBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingRequestDto approveOrRejectBooking(@PathVariable Long bookingId,
                                                 @RequestParam boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.approveOrRejectBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingRequestDto getBookingById(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingRequestDto> getAllBookingsByCurrentUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingRequestDto> getAllBookingsAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsAllItemsByOwner(userId, state);
    }

}
