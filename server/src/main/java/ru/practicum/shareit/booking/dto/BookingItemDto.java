package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingItemDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
}
