package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;
    public static final String HEADER = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(HEADER) Long userId, @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader(HEADER) Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public Collection<Item> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(HEADER) Long userId, @RequestBody ItemDto itemDto) {
        return itemService.create(userId,itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader(HEADER) Long userId, @PathVariable Long itemId, @RequestBody Item item) {
        return itemService.update(userId, itemId, item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
