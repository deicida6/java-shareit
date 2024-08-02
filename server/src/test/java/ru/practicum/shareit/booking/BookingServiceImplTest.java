package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("User")
                .email("user@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(Status.WAITING)
                .build();

        bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        bookingRequestDto = BookingMapper.toBookingRequestDto(booking);
    }

    @Test
    void addBookingSuccess() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingCreateDto.getItemId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingRequestDto result = bookingService.addBooking(bookingCreateDto, user.getId());
        assertNotNull(result);
        assertEquals(result.getStart(), bookingRequestDto.getStart());
        assertEquals(result.getEnd(), bookingRequestDto.getEnd());
        assertEquals(result.getItem(), bookingRequestDto.getItem());
        assertEquals(result.getStatus(), bookingRequestDto.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void addBookingStartEndValidationException() {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(1L)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.addBooking(invalidDto, user.getId()));
        assertEquals("Конец букинга не может равняться или быть раньше начала", exception.getMessage());
    }

    @Test
    void addBookingMissingDatesException() {
        BookingCreateDto invalidDto = BookingCreateDto.builder()
                .start(null)
                .end(null)
                .itemId(1L)
                .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.addBooking(invalidDto, user.getId()));
        assertEquals("Начало или конец аренды должен быть заполнен", exception.getMessage());
    }

    @Test
    void addBookingUserNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingCreateDto, user.getId()));
        assertEquals("Нет юзера с таким ID = " + user.getId(), exception.getMessage());
    }

    @Test
    void addBookingItemNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingCreateDto.getItemId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingCreateDto, user.getId()));
        assertEquals("Нет вещи с таким ID = " + bookingCreateDto.getItemId(), exception.getMessage());
    }

    @Test
    void addBookingItemNotAvailableException() {
        Item unavailableItem = Item.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(false)
                .owner(owner)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingCreateDto.getItemId())).thenReturn(Optional.of(unavailableItem));

        AvailableItemException exception = assertThrows(AvailableItemException.class, () -> bookingService.addBooking(bookingCreateDto, user.getId()));
        assertEquals("Вещь с id " + bookingCreateDto.getItemId() + " не доступна", exception.getMessage());
    }

    @Test
    void approveOrRejectBooking_Success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingRequestDto result = bookingService.approveOrRejectBooking(owner.getId(), booking.getId(), true);
        assertNotNull(result);
        assertEquals(result.getStart(), bookingRequestDto.getStart());
        assertEquals(result.getEnd(), bookingRequestDto.getEnd());
        assertEquals(result.getItem(), bookingRequestDto.getItem());
        assertEquals(result.getStatus(), Status.APPROVED);
        assertEquals(result.getBooker(), bookingRequestDto.getBooker());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void approveOrRejectBookingBookingNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.approveOrRejectBooking(user.getId(), booking.getId(), true));
        assertEquals("Не найдено бронирование с id = " + booking.getId(), exception.getMessage());
    }

    @Test
    void approveOrRejectBookingUserNotFoundException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.approveOrRejectBooking(user.getId(), booking.getId(), true));
        assertEquals("Нет юзера с таким ID = " + user.getId(), exception.getMessage());
    }

    @Test
    void getByIdSuccess() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        BookingRequestDto result = bookingService.getById(booking.getId(), user.getId());

        assertEquals(booking.getId(), result.getId());
        verify(bookingRepository).findById(booking.getId());
    }

    @Test
    void getByIdNotFoundException() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getById(booking.getId(), user.getId()));
        assertEquals("Бронирование на найден с таким id = " + booking.getId(), exception.getMessage());
    }

    @Test
    void getAllBookingsByUserSuccess() {
        String state = "ALL";
        Collection<Booking> bookings = Collections.singletonList(booking);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId())).thenReturn(bookings);

        Collection<BookingRequestDto> result = bookingService.getAllBookingsByUser(user.getId(), state);

        assertNotNull(result);
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(user.getId());
    }

    @Test
    void getAllBookingsByUserInvalidStateException() {
        String state = "INVALID";

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> bookingService.getAllBookingsByUser(user.getId(), state));
        assertEquals("Unknown state: " + state, exception.getMessage());
    }

    @Test
    void getAllBookingsAllItemsByOwnerSuccess() {
        String state = "ALL";
        Collection<Booking> bookings = Collections.singletonList(booking);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId())).thenReturn(bookings);

        Collection<BookingRequestDto> result = bookingService.getAllBookingsAllItemsByOwner(user.getId(), state);

        assertNotNull(result);
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(user.getId());
    }

    @Test
    void getAllBookingsAllItemsByOwnerInvalidStateException() {
        String state = "INVALID";

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> bookingService.getAllBookingsAllItemsByOwner(user.getId(), state));
        assertEquals("Unknown state: " + state, exception.getMessage());
    }

    @Test
    void addBookingWhenUserIsOwnerShouldThrowException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(bookingCreateDto.getItemId())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.addBooking(bookingCreateDto, owner.getId())
        );
        assertEquals("Пользователь не может арендовать вещь, он уже ее владелец", exception.getMessage());
    }

    @Test
    void approveOrRejectBookingStatusNotWaitingException() {
        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .build();
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        AvailableItemException exception = assertThrows(AvailableItemException.class, () ->
                bookingService.approveOrRejectBooking(owner.getId(), booking.getId(), true)
        );
        assertEquals("Нельзя апрувнуть или отклонить, статус не Waiting", exception.getMessage());
    }

    @Test
    void getAllBookingsAllItemsByOwnerUnknownStateException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () ->
                bookingService.getAllBookingsAllItemsByOwner(user.getId(), "UNKNOWN")
        );
        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void getAllBookingsByUserAllState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId())).thenReturn(Collections.singletonList(booking));

        Collection<BookingRequestDto> result = bookingService.getAllBookingsByUser(user.getId(), "ALL");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(user.getId());
    }

    @Test
    void getAllBookingsByUserWaitingState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING)).thenReturn(Collections.singletonList(booking));

        Collection<BookingRequestDto> result = bookingService.getAllBookingsByUser(user.getId(), "WAITING");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.WAITING);
    }

    @Test
    void getAllBookingsByUserRejectedState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED)).thenReturn(Collections.singletonList(booking));

        Collection<BookingRequestDto> result = bookingService.getAllBookingsByUser(user.getId(), "REJECTED");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(user.getId(), Status.REJECTED);
    }

    @Test
    void getAllBookingsByUserInvalidState() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> bookingService.getAllBookingsByUser(user.getId(), "INVALID"));

        assertEquals("Unknown state: INVALID", exception.getMessage());
    }

    @Test
    void getAllBookingsByUserNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByUser(user.getId(), "ALL"));

        assertEquals("Пользователя нет с таким id = " + user.getId(), exception.getMessage());
    }

    @Test
    void getAllBookingsByUserUnknownStateException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () ->
                bookingService.getAllBookingsByUser(user.getId(), "UNKNOWN")
        );
        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }

    @Test
    void getAllBookingsAllItemsByOwnerAllState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId())).thenReturn(Collections.singletonList(booking));

        Collection<BookingRequestDto> result = bookingService.getAllBookingsAllItemsByOwner(owner.getId(), "ALL");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(owner.getId());
    }

    @Test
    void getAllBookingsAllItemsByOwnerWaitingState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING)).thenReturn(Collections.singletonList(booking));

        Collection<BookingRequestDto> result = bookingService.getAllBookingsAllItemsByOwner(owner.getId(), "WAITING");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.WAITING);
    }

    @Test
    void getAllBookingsAllItemsByOwnerRejectedState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED)).thenReturn(Collections.singletonList(booking));

        Collection<BookingRequestDto> result = bookingService.getAllBookingsAllItemsByOwner(owner.getId(), "REJECTED");

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdAndStatusOrderByStartDesc(owner.getId(), Status.REJECTED);
    }

    @Test
    void getAllBookingsAllItemsByOwnerInvalidState() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        InvalidStateException exception = assertThrows(InvalidStateException.class, () -> bookingService.getAllBookingsAllItemsByOwner(owner.getId(), "INVALID"));

        assertEquals("Unknown state: INVALID", exception.getMessage());
    }

    @Test
    void getAllBookingsAllItemsByOwnerNotFoundException() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsAllItemsByOwner(owner.getId(), "ALL"));

        assertEquals("Пользователя нет с таким id = " + owner.getId(), exception.getMessage());
    }
}
