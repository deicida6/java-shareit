package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Validated
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустое")
    private String name;
    @Email
    @NotBlank(message = "Почта не может быть пустая")
    private String email;
}
