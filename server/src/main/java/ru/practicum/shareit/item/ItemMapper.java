package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemMapper {
    public static Item toItem(ItemDto itemDto, User owner) {
        return Item.builder().id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static ItemDto toItemDto(Item item,
                                    BookingItemDto lastBooking,
                                    BookingItemDto nextBooking) {
        return ItemDto.builder().id(item.getId())
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(CommentMapper.toCommentDtoCollection(item.getComments()))
                .build();
    }

    public static Collection<ItemDto> toListItemDto(Collection<Item> items) {
        return items.stream()
                .map(item -> ItemMapper.toItemDto(item,
                                BookingMapper.toBookingItemDto(item.getBookings() == null || item.getBookings().isEmpty() ? null : item.getBookings().getFirst()),
                                BookingMapper.toBookingItemDto(item.getBookings() == null || item.getBookings().isEmpty() ? null : item.getBookings().getLast())))
                .collect(Collectors.toList());
    }
}