package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = User.builder()
                .id(1L)
                .name("TestUserName")
                .email("UserEmail@test.com")
                .build();
        userDto = UserMapper.toUserDto(user);

        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
    }

    @Test
    void getAll() {
        List<UserDto> result = UserMapper.toListUserDto(userService.getAll()).stream().toList();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getFirst().getId(), userDto.getId());
        Assertions.assertEquals(result.getFirst().getName(), userDto.getName());
        Assertions.assertEquals(result.getFirst().getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void create() {
        UserDto result = userService.create(userDto);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), userDto.getId());
        Assertions.assertEquals(result.getName(), userDto.getName());
        Assertions.assertEquals(result.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void update() {
        User result = userService.update(user.getId(), user);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), userDto.getId());
        Assertions.assertEquals(result.getName(), userDto.getName());
        Assertions.assertEquals(result.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getById() {
        User result = userService.getById(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), userDto.getId());
        Assertions.assertEquals(result.getName(), userDto.getName());
        Assertions.assertEquals(result.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void delete() {
        userService.delete(user.getId());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}