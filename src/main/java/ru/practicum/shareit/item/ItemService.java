package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    public ItemDto create(Long userId, ItemDto itemDto);

    public Item update(Long userId, Long itemId, Item item);

    public ItemDto getById(Long userId, Long itemId);

    public Collection<Item> searchItem(String text);

    public Collection<ItemDto> getAll(Long userId);

    CommentDto addComment(Long userId, Long itemId, @Valid CommentDto commentDto);

}
