package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    public User create(User user);

    public User update(Long userId, User user);

    public User delete(Long userId);

    public User getById(Long userId);

    public Collection<User> getAll();
}
