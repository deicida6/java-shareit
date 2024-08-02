package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {

        return client.addItem(ownerId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return client.updateItem(userId, itemId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable Long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllById(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getAllItemByUserId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return client.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody CommentRequestDto comment,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        return client.addComment(comment, userId, itemId);
    }
}
