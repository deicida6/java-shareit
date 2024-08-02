package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {

    @Test
    void testToItem() {
        User owner = User.builder().id(1L).build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(itemDto, owner);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertEquals(owner, item.getOwner());
    }

    @Test
    void testToItemDtoWithBookings() {
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        BookingItemDto lastBooking = BookingItemDto.builder().id(1L).build();
        BookingItemDto nextBooking = BookingItemDto.builder().id(2L).build();

        ItemDto itemDto = ItemMapper.toItemDto(item, lastBooking, nextBooking);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(lastBooking, itemDto.getLastBooking());
        assertEquals(nextBooking, itemDto.getNextBooking());
    }

    @Test
    void testToItemDtoWithNullBookings() {
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        ItemDto itemDto = ItemMapper.toItemDto(item, null, null);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void testToListItemDto() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .build();

        List<Item> items = List.of(item1, item2);

        Collection<ItemDto> itemDtos = ItemMapper.toListItemDto(items);

        assertEquals(2, itemDtos.size());

        ItemDto dto1 = itemDtos.stream().filter(dto -> dto.getId().equals(1L)).findFirst().orElse(null);
        assertEquals(item1.getId(), dto1.getId());
        assertEquals(item1.getName(), dto1.getName());
        assertEquals(item1.getDescription(), dto1.getDescription());
        assertEquals(item1.getAvailable(), dto1.getAvailable());

        ItemDto dto2 = itemDtos.stream().filter(dto -> dto.getId().equals(2L)).findFirst().orElse(null);
        assertEquals(item2.getId(), dto2.getId());
        assertEquals(item2.getName(), dto2.getName());
        assertEquals(item2.getDescription(), dto2.getDescription());
        assertEquals(item2.getAvailable(), dto2.getAvailable());
    }

    @Test
    void testToListItemDtoEmptyList() {
        Collection<ItemDto> itemDtos = ItemMapper.toListItemDto(Collections.emptyList());

        assertEquals(0, itemDtos.size());
    }
}