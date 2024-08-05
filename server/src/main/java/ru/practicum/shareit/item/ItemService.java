package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    Item update(Long userId, Long itemId, Item item);

    ItemDto getById(Long userId, Long itemId);

    Collection<Item> searchItem(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    Collection<ItemDto> getAll(Long userId);
}
