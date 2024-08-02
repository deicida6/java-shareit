package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ReqItemDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                         @Valid @RequestBody ReqItemDto createDto) {
        return client.createRequest(authorId, createDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long authorId) {
        return client.getRequestsByAuthorId(authorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsAnotherUsers(@RequestParam(required = false, defaultValue = "0") int from,
                                                          @RequestParam(required = false, defaultValue = "10") int size,
                                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getRequestsOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        return client.getRequestById(requestId, userId);
    }
}
