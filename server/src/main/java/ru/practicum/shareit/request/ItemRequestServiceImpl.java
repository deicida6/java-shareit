package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        itemRequestDto.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(
                itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user)));
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequestByOwner(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        return itemRequestRepository.findAllByRequesterId(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> getAllItemRequestToOtherUser(Pageable pageable) {
        return itemRequestRepository.findAll(pageable).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        return ItemRequestMapper.toItemRequestDto(
                itemRequestRepository.findByIdOrderByCreatedAsc(requestId).orElseThrow(() ->
                new NotFoundException("Запроса нет от пользователя с ID  " + requestId)));
    }
}