package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Пользователь с таким e-mail = " + userDto.getEmail() + " уже существует!");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(userDto)));
    }

    @Override
    @Transactional
    public User update(Long userId, User newUser) {
        User oldUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Пользователь с таким e-mail = " + newUser.getEmail() + " уже существует!");
        }
        newUser.setName(Optional.ofNullable(newUser.getName()).orElse(oldUser.getName()));
        newUser.setEmail(Optional.ofNullable(newUser.getEmail()).orElse(oldUser.getEmail()));
        newUser.setId(Optional.ofNullable(newUser.getId()).orElse(oldUser.getId()));
        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        } else {
            log.error("Пользователь с таким Id {} не найден", userId);
            throw new NotFoundException("Пользователь с таким Id " + userId + " не найден");
        }
    }

    @Override
    public User getById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

}
