package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getAllItemRequestByOwner(Long userId);

    Collection<ItemRequestDto> getAllItemRequestToOtherUser(Pageable pageable);

    ItemRequestDto getItemRequestById(Long requestId);

}