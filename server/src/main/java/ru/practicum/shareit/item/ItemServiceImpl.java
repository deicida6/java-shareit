package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> {
                throw new NotFoundException("Запрос с id = " + itemDto.getRequestId() + " не найден!");
            });
        }
        return ItemMapper.toItemDto(itemRepository.save(
                Item.builder()
                        .name(itemDto.getName())
                        .owner(user)
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .request(itemRequest)
                        .build()
        ), null, null);
    }

    @Override
    public Item update(Long userId, Long itemId, Item newItem) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Объект с таким Id " + itemId + " не найден"));
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Не найден объект");
        }
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (newItem.getId() == null) {
            newItem.setId(itemId);
        }

        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }
        newItem.setOwner(oldItem.getOwner());
        return itemRepository.save(newItem);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Объект с таким Id " + itemId + " не найден"));
        return ItemMapper.toItemDto(item,
                BookingMapper.toBookingItemDto(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartBeforeAndStatusOrderByStartDesc(itemId, userId, LocalDateTime.now(), Status.APPROVED).orElse(null)),
                BookingMapper.toBookingItemDto(bookingRepository.findFirstByItemIdAndItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(itemId, userId, LocalDateTime.now(), Status.APPROVED).orElse(null)));
    }

    @Override
    public Collection<Item> searchItem(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(text,text)
                .stream()
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getAll(Long userId) {
       userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        return ItemMapper.toListItemDto(itemRepository.findAllByOwnerId(userId));
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Автор не найден с таким id = " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена с таким id = " + itemId));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Пользователь с id = " + userId + " не брал в аренду вещь с id = " + itemId);
        }
        Comment comment = CommentMapper.toComment(commentDto, author, item, LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}