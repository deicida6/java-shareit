package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.Collection;

@Data
@Validated
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private Collection<CommentDto> comments;
}
