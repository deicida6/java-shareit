package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private ItemService itemService;
    private ItemRepository itemRepository;
    private CommentRepository commentRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRequestRepository itemRequestRepository;
    private Booking booking;
    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                commentRepository,
                bookingRepository,
                itemRequestRepository);
        user = User.builder()
                .id(1L)
                .name("Test1")
                .email("Test1@test.com")
                .build();
        when(userRepository.save(any())).thenReturn(user);
        owner = User.builder()
                .id(2L)
                .name("Test2")
                .email("Test2@test.com")
                .build();
        when(userRepository.save(any())).thenReturn(owner);
        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.now())
                .description("Test")
                .build();
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        item = Item.builder()
                .id(1L)
                .name("Test")
                .description("Test")
                .request(itemRequest)
                .available(true)
                .owner(owner)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .booker(user)
                .item(item)
                .build();
        when(bookingRepository.save(any())).thenReturn(booking);
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test")
                .item(item)
                .author(owner)
                .created(LocalDateTime.now())
                .build();
        when(commentRepository.save(any())).thenReturn(comment);
        commentDto = CommentMapper.toCommentDto(comment);
        itemDto = ItemMapper.toItemDto(item,
                null,null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.findAll()).thenReturn(List.of(item));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));
        when(itemRepository.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString())).thenReturn(List.of(item));
    }

    @Test
    void getAll() {
        List<ItemDto> result = itemService.getAll(owner.getId()).stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getName(), itemDto.getName());
        Assertions.assertEquals(result.getFirst().getDescription(), itemDto.getDescription());
        Assertions.assertEquals(result.getFirst().getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(result.getFirst().getRequestId(), itemDto.getRequestId());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong());
    }

    @Test
    void create() {
        ItemDto result = itemService.create(user.getId(), itemDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), itemDto.getName());
        Assertions.assertEquals(result.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(result.getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(result.getId(), itemDto.getId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void update() {
        Item result = itemService.update(owner.getId(), item.getId(), item);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), item.getName());
        Assertions.assertEquals(result.getDescription(), item.getDescription());
        Assertions.assertEquals(result.getAvailable(), item.getAvailable());
        Assertions.assertEquals(result.getId(), item.getId());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    void addComment() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(),
                anyLong(),
                any(),
                any())).thenReturn((List.of(new Booking())));
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CommentDto result = itemService.addComment(1L, 1L, commentDto);
        Assertions.assertNotNull(result);
        verify(itemRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    void getById() {
        ItemDto result = itemService.getById(user.getId(), item.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getName(), itemDto.getName());
        Assertions.assertEquals(result.getDescription(), itemDto.getDescription());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void searchItem() {
        List<ItemDto> result = ItemMapper.toListItemDto(itemService.searchItem("text")).stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getName(), itemDto.getName());
        Assertions.assertEquals(result.getFirst().getDescription(), itemDto.getDescription());
        Assertions.assertEquals(result.getFirst().getAvailable(), itemDto.getAvailable());
        verify(itemRepository, times(1)).findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString());
    }
}