package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;
    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingRequestDto addBooking(@RequestBody BookingCreateDto bookingCreateDto,
                                       @RequestHeader(HEADER) Long userId) {
        return bookingService.addBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingRequestDto approveOrRejectBooking(@RequestHeader(HEADER) Long userId,
                                                    @PathVariable Long bookingId,
                                                    @RequestParam Boolean approved) {
        return bookingService.approveOrRejectBooking(userId, bookingId, approved);
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
