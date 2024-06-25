package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userMap = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        log.info("Пользователь создан с id {}", user.getId());
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        userMap.replace(userId,user);
        log.info("Пользователь обновлен с id {}", userId);
        return user;
    }

    @Override
    public User delete(Long userId) {
        log.info("Пользователь удален с id {}", userId);
        return userMap.remove(userId);
    }

    @Override
    public User getById(Long userId) {
        return userMap.get(userId);
    }

    @Override
    public Collection<User> getAll() {
        return userMap.values();
    }

    private Long getNextId() {
        return ++id;
    }
}
