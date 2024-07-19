package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
            return UserMapper.toUserDto(userRepository.save(UserMapper.fromUserDto(userDto)));
    }

    @Override
    @Transactional
    public User update(Long userId, User newUser) {
        User oldUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с таким Id " + userId + " не найден"));
                if (newUser.getName() == null) {
                    newUser.setName(oldUser.getName());
                }
                if (newUser.getEmail() == null) {
                    newUser.setEmail(oldUser.getEmail());
                }
                if (newUser.getId() == null) {
                    newUser.setId(oldUser.getId());
                }
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
