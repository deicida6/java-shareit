package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemNameDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserNameDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private User user;
    private Item item;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void shouldMapBookingToBookingRequestDto() {
        BookingRequestDto bookingRequestDto = BookingMapper.toBookingRequestDto(booking);

        assertNotNull(bookingRequestDto);
        assertEquals(booking.getId(), bookingRequestDto.getId());
        assertEquals(booking.getStart(), bookingRequestDto.getStart());
        assertEquals(booking.getEnd(), bookingRequestDto.getEnd());
        assertEquals(booking.getStatus(), bookingRequestDto.getStatus());

        ItemNameDto itemNameDto = bookingRequestDto.getItem();
        assertNotNull(itemNameDto);
        assertEquals(item.getId(), itemNameDto.getId());
        assertEquals(item.getName(), itemNameDto.getName());

        UserNameDto userNameDto = bookingRequestDto.getBooker();
        assertNotNull(userNameDto);
        assertEquals(user.getId(), userNameDto.getId());
        assertEquals(user.getName(), userNameDto.getName());
    }

    @Test
    void shouldReturnBookingItemDtoWhenBookingIsNotNull() {
        BookingItemDto bookingItemDto = BookingMapper.toBookingItemDto(booking);

        assertNotNull(bookingItemDto);
        assertEquals(booking.getId(), bookingItemDto.getId());
        assertEquals(booking.getStart(), bookingItemDto.getStart());
        assertEquals(booking.getEnd(), bookingItemDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingItemDto.getBookerId());
    }

    @Test
    void shouldReturnNullWhenBookingIsNull() {
        BookingItemDto bookingItemDto = BookingMapper.toBookingItemDto(null);

        assertNull(bookingItemDto);
    }

    @Test
    void shouldMapBookingCollectionToBookingRequestDtoCollection() {
        Collection<Booking> bookings = Arrays.asList(booking);
        Collection<BookingRequestDto> bookingRequestDtos = BookingMapper.toListBookingRequestDto(bookings);

        assertNotNull(bookingRequestDtos);
        assertEquals(1, bookingRequestDtos.size());

        BookingRequestDto bookingRequestDto = bookingRequestDtos.iterator().next();
        assertEquals(booking.getId(), bookingRequestDto.getId());
    }

    @Test
    void shouldReturnEmptyCollectionWhenBookingsIsEmpty() {
        Collection<Booking> bookings = Collections.emptyList();
        Collection<BookingRequestDto> bookingRequestDtos = BookingMapper.toListBookingRequestDto(bookings);

        assertNotNull(bookingRequestDtos);
        assertTrue(bookingRequestDtos.isEmpty());
    }

    @Test
    void shouldMapBookingCreateDtoToBooking() {
        Booking mappedBooking = BookingMapper.toBooking(bookingCreateDto, user, item);

        assertNotNull(mappedBooking);
        assertEquals(bookingCreateDto.getStart(), mappedBooking.getStart());
        assertEquals(bookingCreateDto.getEnd(), mappedBooking.getEnd());
        assertEquals(user, mappedBooking.getBooker());
        assertEquals(item, mappedBooking.getItem());
        assertEquals(Status.WAITING, mappedBooking.getStatus());
    }
}