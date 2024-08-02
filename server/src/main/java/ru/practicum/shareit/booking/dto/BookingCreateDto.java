package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingCreateDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
