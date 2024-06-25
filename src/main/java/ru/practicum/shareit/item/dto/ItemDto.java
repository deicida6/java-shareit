package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@EqualsAndHashCode
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустое")
    private String name;
    @NotBlank(message = "Описание не может быть пустое")
    private String description;
    private Boolean available;
    private Long request;
}
