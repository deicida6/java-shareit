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
    private final UserStorage userDto;

    @Override
    public User create(User user) {
        if(isEmailUnique(user.getEmail())){
            return userDto.create(user);
        } else {
            log.error("Пользователь с таким Email {} уже существует", user.getEmail());
            throw new AlreadyExistsException("Пользователь с таким Email " + user.getEmail() + " уже существует");
        }
    }

    @Override
    public User update(Long userId, User user) {
        if(userDto.getById(userId) != null) {
            if(userDto.getById(userId).getEmail().equals(user.getEmail())) {
                if(user.getName() == null) {
                    user.setName(userDto.getById(userId).getName());
                }
                if(user.getId() == null) {
                    user.setId(userId);
                }
                return userDto.update(userId, user);
            }
            if(isEmailUnique(user.getEmail())) {
                if(user.getEmail() == null) {
                    user.setEmail(userDto.getById(userId).getEmail());
                }
                if(user.getName() == null) {
                    user.setName(userDto.getById(userId).getName());
                }
                if(user.getId() == null) {
                    user.setId(userId);
                }
                return userDto.update(userId, user);
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
        if (userDto.getById(userId) != null){
            return userDto.delete(userId);
        } else {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователь с таким Id " + userId + " не найден");
        }
    }

    @Override
    public User getById(Long userId) {
        if (userDto.getById(userId) != null){
            return userDto.getById(userId);
        } else {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователь с таким Id " + userId + " не найден");
        }
    }

    @Override
    public Collection<User> getAll() {
        return userDto.getAll();
    }

    private boolean isEmailUnique(String email) {
        for (User user : userDto.getAll()) {
            if(user.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }
}
