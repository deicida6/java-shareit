package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemNameDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserNameDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingRequestDto toBookingRequestDto(Booking booking) {
        return BookingRequestDto.builder().id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemNameDto.builder().id(booking.getItem().getId()).name(booking.getItem().getName()).build())
                .booker(UserNameDto.builder().id(booking.getBooker().getId()).name(booking.getBooker().getName()).build())
                .status(booking.getStatus())
                .build();
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingItemDto.builder().id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static Collection<BookingRequestDto> toListBookingRequestDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingRequestDto).collect(Collectors.toList());
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto, User user, Item item) {
        return Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }
}
