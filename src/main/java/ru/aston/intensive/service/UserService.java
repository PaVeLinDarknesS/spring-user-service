package ru.aston.intensive.service;

import ru.aston.intensive.entity.UserEntity;

import java.util.List;

public interface UserService {

    List<UserEntity> findAll();

    UserEntity findById(Long id);

    UserEntity create(UserEntity userEntity);

    UserEntity update(long userId, UserEntity user);

    void delete(Long id);
}
