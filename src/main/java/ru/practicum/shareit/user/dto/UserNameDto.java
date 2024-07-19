package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class UserNameDto {
    private Long id;
    private String name;
}
