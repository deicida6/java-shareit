package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    public static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(HEADER) Long userId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getItemRequestsByOwner(@RequestHeader(HEADER) Long userId) {
        return itemRequestService.getAllItemRequestByOwner(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getItemRequestsAllButOwner(@RequestHeader(HEADER) Long userId,
                                                           @RequestParam(defaultValue = "0") int from,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        return itemRequestService.getAllItemRequestToOtherUser(pageable);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}
