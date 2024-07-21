package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Validated
@Builder(toBuilder = true)
public class BookingCreateDto {
    private Long itemId;
    @NotNull
    @FutureOrPresent(message = "Дата должна быть в настоящем времени или будущем")
    private LocalDateTime start;
    @NotNull
    @Future(message = "Дата должна быть в будущем")
    private LocalDateTime end;
}
