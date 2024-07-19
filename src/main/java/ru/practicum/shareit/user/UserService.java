package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    public UserDto create(UserDto userDto);

    public User update(Long userId, User user);

    public void delete(Long userId);

    public User getById(Long userId);

    public Collection<User> getAll();
}
