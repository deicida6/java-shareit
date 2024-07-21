package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingService bookingService;
    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingRequestDto addBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                     @RequestHeader (HEADER) Long userId) {
        return bookingService.addBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingRequestDto approveOrRejectBooking(@PathVariable Long bookingId,
                                                 @RequestParam boolean approved,
                                                 @RequestHeader(HEADER) Long userId) {
        return bookingService.approveOrRejectBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingRequestDto getBookingById(@PathVariable Long bookingId,
                                         @RequestHeader(HEADER) Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingRequestDto> getAllBookingsByCurrentUser(@RequestHeader(HEADER) Long userId,
                                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingRequestDto> getAllBookingsAllItemsByOwner(@RequestHeader(HEADER) Long userId,
                                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsAllItemsByOwner(userId, state);
    }

}
