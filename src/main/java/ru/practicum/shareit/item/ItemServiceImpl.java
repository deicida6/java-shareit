package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NoFinishBookingForCommentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.fromItemDto(itemDto, user)));
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
        ItemDto itemDto = ItemMapper.toItemDto(item);
        Collection<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        if (comments.isEmpty()) {
            itemDto.setComments(new ArrayList<>());
        } else {
            itemDto.setComments(CommentMapper.toCommentDtoCollection(comments));
        }
        if (!userId.equals(item.getOwner().getId())) {
            return itemDto;
        }
        bookingRepository.findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(
                        itemDto.getId(), Status.APPROVED, LocalDateTime.now())
                .ifPresent(itemDto::setLastBooking);
        bookingRepository.findFirstByItemIdAndStatusAndStartIsAfterOrderByStart(
                        itemDto.getId(), Status.APPROVED, LocalDateTime.now())
                .ifPresent(itemDto::setNextBooking);
        return itemDto;
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
       Collection<ItemDto> itemDtos = ItemMapper.toListItemDto(itemRepository.findByOwnerId(userId));
        itemDtos.forEach(itemDto -> {
            bookingRepository.findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(
                            itemDto.getId(), Status.APPROVED, LocalDateTime.now())
                    .ifPresent(itemDto::setLastBooking);
            bookingRepository.findFirstByItemIdAndStatusAndStartIsAfterOrderByStart(
                            itemDto.getId(), Status.APPROVED, LocalDateTime.now())
                    .ifPresent(itemDto::setNextBooking);
            Collection<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
            if (comments.isEmpty()) {
                itemDto.setComments(new ArrayList<>());
            } else {
                itemDto.setComments(CommentMapper.toCommentDtoCollection(comments));
            }
        });
        return itemDtos;
    }

    @Transactional
    @Override
    public CommentDto addComment(Long userId, Long itemId, @Valid CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Автор не найден с таким id = " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь не найдена с таким id = " + itemId));
        bookingRepository.findFirstByBookerIdAndItemIdAndStatusIsAndEndIsBeforeOrderByEndDesc(
                userId, itemId, Status.APPROVED, LocalDateTime.now()).orElseThrow(() ->
                new NoFinishBookingForCommentException("Не закончен букинг."));

        Comment comment = CommentMapper.toComment(commentDto, author, item, LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
