package ru.aston.intensive.util.mapper;

import ru.aston.intensive.dto.UserDto;
import ru.aston.intensive.entity.UserEntity;

public class UserMapper {

    public static UserEntity dtoToEntity(UserDto dto) {
        return new UserEntity(dto.getName(), dto.getEmail(), dto.getAge());
    }

    public static UserDto entityToDto(UserEntity entity) {
        return new UserDto(entity.getName(), entity.getEmail(), entity.getAge());
    }
}
