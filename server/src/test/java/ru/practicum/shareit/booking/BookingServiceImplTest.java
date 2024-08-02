package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;
    private BookingCreateDto bookingCreateDto;
    private User user;
    private User owner;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        user = User.builder()
                .id(1L)
                .name("Test1")
                .email("Test@test.com")
                .build();
        when(userRepository.save(any())).thenReturn(user);
        owner = User.builder()
                .id(2L)
                .name("Test2")
                .email("Test@test.com")
                .build();
        when(userRepository.save(any())).thenReturn(owner);
        Item item = Item.builder()
                .id(1L)
                .name("Test")
                .description("Test")
                .request(null)
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.save(any())).thenReturn(item);
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .booker(user)
                .item(item)
                .build();
        bookingRequestDto = BookingMapper.toBookingRequestDto(booking);

        bookingCreateDto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(List.of(booking));
    }

    @Test
    void addBooking() {
        BookingRequestDto result = bookingService.addBooking(bookingCreateDto, user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getStart(), bookingRequestDto.getStart());
        Assertions.assertEquals(result.getEnd(), bookingRequestDto.getEnd());
        Assertions.assertEquals(result.getItem(), bookingRequestDto.getItem());
        Assertions.assertEquals(result.getStatus(), bookingRequestDto.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void approveOrRejectBooking() {
        BookingRequestDto result = bookingService.approveOrRejectBooking(owner.getId(), booking.getId(), true);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getStart(), bookingRequestDto.getStart());
        Assertions.assertEquals(result.getEnd(), bookingRequestDto.getEnd());
        Assertions.assertEquals(result.getItem(), bookingRequestDto.getItem());
        Assertions.assertEquals(result.getStatus(), Status.APPROVED);
        Assertions.assertEquals(result.getBooker(), bookingRequestDto.getBooker());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void getById() {
        BookingRequestDto result = bookingService.getById(booking.getId(), user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getStart(), bookingRequestDto.getStart());
        Assertions.assertEquals(result.getEnd(), bookingRequestDto.getEnd());
        Assertions.assertEquals(result.getItem(), bookingRequestDto.getItem());
        Assertions.assertEquals(result.getStatus(), bookingRequestDto.getStatus());
        Assertions.assertEquals(result.getBooker(), bookingRequestDto.getBooker());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllBookingsByUser() {
        List<BookingRequestDto> result = bookingService.getAllBookingsByUser(user.getId(), "ALL").stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getStart(), bookingRequestDto.getStart());
        Assertions.assertEquals(result.getFirst().getEnd(), bookingRequestDto.getEnd());
        Assertions.assertEquals(result.getFirst().getItem(), bookingRequestDto.getItem());
        Assertions.assertEquals(result.getFirst().getStatus(), bookingRequestDto.getStatus());
        Assertions.assertEquals(result.getFirst().getBooker(), bookingRequestDto.getBooker());
        verify(bookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(anyLong());
    }

    @Test
    void getAllBookingsAllItemsByOwner() {
        List<BookingRequestDto> result = bookingService.getAllBookingsAllItemsByOwner(owner.getId(), "ALL").stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getStart(), bookingRequestDto.getStart());
        Assertions.assertEquals(result.getFirst().getEnd(), bookingRequestDto.getEnd());
        Assertions.assertEquals(result.getFirst().getItem(), bookingRequestDto.getItem());
        Assertions.assertEquals(result.getFirst().getStatus(), bookingRequestDto.getStatus());
        Assertions.assertEquals(result.getFirst().getBooker(), bookingRequestDto.getBooker());
        verify(bookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(anyLong());
    }
}