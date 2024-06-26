package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Validated
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank(message = "Имя не может быть пустое")
    private String name;
    @NotBlank(message = "Описание не может быть пустое")
    private String description;
    @NotNull(message = "Не может отсуствовать значение доступности")
    private Boolean available;
    @NotNull(message = "Не может отсуствовать автор")
    private Long owner;
    private Long request;
}
