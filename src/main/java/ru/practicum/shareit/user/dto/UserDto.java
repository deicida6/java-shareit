package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
