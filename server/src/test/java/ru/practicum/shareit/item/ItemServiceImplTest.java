package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("testuser@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .description("Request Description")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        itemDto = ItemMapper.toItemDto(item, null, null);

        comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Comment Text")
                .authorName(owner.getName())
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(user.getId(), itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createWithInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.create(999L, itemDto);
        });

        assertEquals("Пользователь с таким Id 999 не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.update(owner.getId(), item.getId(), item);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateWithInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.update(999L, item.getId(), item);
        });

        assertEquals("Пользователь с таким Id 999 не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateWithInvalidItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.update(user.getId(), 999L, item);
        });

        assertEquals("Объект с таким Id 999 не найден", exception.getMessage());
        verify(itemRepository, times(1)).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateWithInvalidOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.update(user.getId(), item.getId(), item);
        });

        assertEquals("Не найден объект", exception.getMessage());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemService.getById(user.getId(), item.getId());

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void getByIdWithInvalidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.getById(999L, item.getId());
        });

        assertEquals("Пользователь с таким Id 999 не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void searchItem() {
        when(itemRepository.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString()))
                .thenReturn(Collections.singletonList(item));

        Collection<Item> result = itemService.searchItem("Test");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString());
    }

    @Test
    void searchItemWithEmptyText() {
        Collection<Item> result = itemService.searchItem("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemRepository, never()).findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString());
    }

    @Test
    void getAll() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        Collection<ItemDto> result = itemService.getAll(owner.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findAllByOwnerId(owner.getId());
    }

    @Test
    void addComment() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(Booking.builder().build()));
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CommentDto result = itemService.addComment(1L, 1L, commentDto);
        assertNotNull(result);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(user.getId(), itemDto));
    }

    @Test
    void updateShouldReturnUpdatedItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.update(owner.getId(), item.getId(), item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(user.getId(), item.getId(), item));
    }

    @Test
    void updateShouldThrowNotFoundException_WhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(user.getId(), item.getId(), item));
    }

    @Test
    void getByIdShouldReturnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemService.getById(user.getId(), item.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void getByIdShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(user.getId(), item.getId()));
    }

    @Test
    void getByIdShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(user.getId(), item.getId()));
    }

    @Test
    void searchItemShouldReturnItems() {
        when(itemRepository.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(anyString(), anyString()))
                .thenReturn(Collections.singletonList(item));

        Collection<Item> result = itemService.searchItem("Item");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.iterator().next().getId());
    }

    @Test
    void searchItemShouldReturnEmptyCollectionWhenTextIsEmpty() {
        Collection<Item> result = itemService.searchItem("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllShouldReturnItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.singletonList(item));

        Collection<ItemDto> result = itemService.getAll(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.iterator().next().getId());
    }

    @Test
    void getAllShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getAll(user.getId()));
    }

    @Test
    void addCommentShouldReturnComment() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mock(Booking.class)));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
    }

    @Test
    void addCommentShouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void addCommentShouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }

    @Test
    void addCommentShouldThrowValidationExceptionWhenNoBookingFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
    }
}