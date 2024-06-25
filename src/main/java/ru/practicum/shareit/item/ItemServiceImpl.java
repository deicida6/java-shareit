package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AvailableItemException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item create(Long userId, Item item) {
        if (userId == null) {
            log.error("Не указан владелец");
            throw new RuntimeException("Не указан владелец");
        }
        if (userStorage.getById(userId) == null) {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователя с таким Id " + userId + " не существует");
        }
        if (item.getAvailable() == null) {
            log.error("Не указан статус объекта при создании");
            throw new AvailableItemException("Не указан статус объекта при создании");
        }
        return itemStorage.create(userId,item);
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        if (userStorage.getById(userId) == null) {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователь с таким Id " + userId + " не найден");
        }
        if (itemStorage.getById(itemId) == null) {
            log.error("Объект с таким Id {} не найден", itemId);
            throw new NotFoundException("Объект с таким Id " + itemId + " не найден");
        }
        if (!itemStorage.getById(itemId).getOwner().equals(userId)) {
            log.error("У пользователя {}, нет такого объекта {}", userId, itemId);
            throw new NotFoundException("Не найден объект");
        }
        if (item.getName() == null) {
            item.setName(itemStorage.getById(itemId).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(itemStorage.getById(itemId).getDescription());
        }
        if (item.getId() == null) {
            item.setId(itemId);
        }

        if (item.getAvailable() == null) {
            item.setAvailable(itemStorage.getById(itemId).getAvailable());
        }
        item.setOwner(itemStorage.getById(itemId).getOwner());
        return itemStorage.update(itemId,item);
    }

    @Override
    public Item getById(Long itemId) {
        if (itemStorage.getById(itemId) != null) {
            return itemStorage.getById(itemId);
        } else {
            log.error("Объект с таким Id {} не найден", itemId);
            throw new NotFoundException("Объект с таким Id " + itemId + " не найден");
        }
    }

    @Override
    public Collection<Item> getByName(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.getByName(text);
    }

    @Override
    public Collection<Item> getAll(Long userId) {
        if (userStorage.getById(userId) != null) {
            return itemStorage.getAll(userId);
        } else {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователь с таким Id " + userId + " не найден");
        }
    }
}
