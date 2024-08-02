package ru.practicum.shareit.request.model;


/**
 * TODO Sprint add-item-requests.
 */

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    private LocalDateTime created;
    @OneToMany
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private Collection<Item> items;
}