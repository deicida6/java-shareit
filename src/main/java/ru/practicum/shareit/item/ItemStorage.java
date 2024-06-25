package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    public Item create(Long userId, Item item);

    public Item update(Long itemId, Item item);

    public Item getById(Long itemId);

    public Collection<Item> getByName(String text);

    public Collection<Item> getAll(Long userId);
}
