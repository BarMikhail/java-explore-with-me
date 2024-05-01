package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {

        User user = UserMapper.toUser(userDto);
        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        checkUser(userId);
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public List<UserDto> getUsers(List<Long> id, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (id.isEmpty()) {
            return UserMapper.toUserDtoList(userRepository.findAll(pageRequest));
        } else {
            return UserMapper.toUserDtoList(userRepository.findByIdIn(id, pageRequest));
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого юзера"));
    }
}
