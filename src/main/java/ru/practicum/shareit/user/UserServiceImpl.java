package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public User create(User user) {
        if (isEmailUnique(user.getEmail())) {
            return userStorage.create(user);
        } else {
            log.error("Пользователь с таким Email {} уже существует", user.getEmail());
            throw new AlreadyExistsException("Пользователь с таким Email " + user.getEmail() + " уже существует");
        }
    }

    @Override
    public User update(Long userId, User user) {
        if (userStorage.getById(userId) != null) {
            if (userStorage.getById(userId).getEmail().equals(user.getEmail())) {
                if (user.getName() == null) {
                    user.setName(userStorage.getById(userId).getName());
                }
                if (user.getId() == null) {
                    user.setId(userId);
                }
                return userStorage.update(userId, user);
            }
            if (isEmailUnique(user.getEmail())) {
                if (user.getEmail() == null) {
                    user.setEmail(userStorage.getById(userId).getEmail());
                }
                if (user.getName() == null) {
                    user.setName(userStorage.getById(userId).getName());
                }
                if (user.getId() == null) {
                    user.setId(userId);
                }
                return userStorage.update(userId, user);
            } else {
                log.error("Пользователь с таким Email {} уже существует", user.getEmail());
                throw new AlreadyExistsException("Пользователь с таким Email " + user.getEmail() + " уже существует");
            }
        } else {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователь с таким Id " + userId + " не найден");
        }
    }

    @Override
    public User delete(Long userId) {
        if (userStorage.getById(userId) != null) {
            return userStorage.delete(userId);
        } else {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователь с таким Id " + userId + " не найден");
        }
    }

    @Override
    public User getById(Long userId) {
        User user = userStorage.getById(userId);
        if (user == null) {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователь с таким Id " + userId + " не найден");
        }
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    private boolean isEmailUnique(String email) {
        for (User user : userStorage.getAll()) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }
}
