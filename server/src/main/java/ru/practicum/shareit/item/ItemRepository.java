package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCase(String name, String description);

    Collection<Item> findAllByOwnerId(Long ownerId);
}