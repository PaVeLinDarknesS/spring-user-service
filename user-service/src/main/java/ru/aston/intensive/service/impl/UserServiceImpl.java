package ru.aston.intensive.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.intensive.dto.UserEvent;
import ru.aston.intensive.entity.UserEntity;
import ru.aston.intensive.enumerated.UserStatus;
import ru.aston.intensive.exception.EmailExistingException;
import ru.aston.intensive.exception.UserNotFoundException;
import ru.aston.intensive.repository.UserRepository;
import ru.aston.intensive.service.UserKafkaProducer;
import ru.aston.intensive.service.UserService;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserKafkaProducer userKafkaProducer;
    private final UserRepository userRepository;

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("user not found with id = {}", id);
                    return new UserNotFoundException("Not found user with id = " + id);
                });
    }

    @Override
    @Transactional
    public UserEntity create(UserEntity userEntity) {
        if (userRepository.findByEmail(userEntity.getEmail()).isPresent()) {
            log.warn("user with email already exist");
            throw new EmailExistingException("User already exist with email = " + userEntity.getEmail());
        }
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("created user with id = {}", savedUser.getId());
        userKafkaProducer.sendUserEvent(new UserEvent(UserStatus.CREATED, savedUser.getEmail()));
        return savedUser;
    }

    @Override
    @Transactional
    public UserEntity update(long userId, UserEntity user) {
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        if (optionalUserEntity.isEmpty()) {
            log.warn("Can't found user for update with id = {}", userId);
            throw new UserNotFoundException("User not exist with id = " + userId);
        }
        UserEntity oldUser = optionalUserEntity.get();

        if (user.getEmail() != null && !oldUser.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                log.warn("Can't set existing in db email");
                throw new EmailExistingException("User already exist with email = " + user.getEmail());
            }
        }

        oldUser.setEmail(user.getEmail());
        oldUser.setName(user.getName());
        oldUser.setAge(user.getAge());

        return oldUser;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<UserEntity> deletedUser = userRepository.findById(id);
        if (deletedUser.isEmpty()) {
            log.warn("Can't found user for delete with id = {}", id);
            throw new UserNotFoundException("User not exist with id = " + id);
        }
        UserEntity user = deletedUser.get();
        userRepository.delete(user);
        userKafkaProducer.sendUserEvent(new UserEvent(UserStatus.DELETED, user.getEmail()));
    }
}
