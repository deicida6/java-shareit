package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item create(Long userId, Item item) {
        item.setOwner(userId);
        item.setId(getNextId());
        itemMap.put(item.getId(), item);
        log.info("Объект создан с id {}", item.getId());
        return itemMap.get(item.getId());
    }

    @Override
    public Item update(Long itemId, Item item) {
        itemMap.replace(itemId,item);
        log.info("Объект обновлен с id {}", item.getId());
        return itemMap.get(itemId);
    }

    @Override
    public Item getById(Long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public Collection<Item> getByName(String text) {
        return itemMap.values().stream()
                .filter(i -> (i.getDescription().toLowerCase().contains(text.toLowerCase())
                        || i.getName().toLowerCase().contains(text.toLowerCase())) && i.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> getAllByUserId(Long userId) {
        return itemMap.values().stream()
                .filter(it -> it.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        return ++id;
    }

}
