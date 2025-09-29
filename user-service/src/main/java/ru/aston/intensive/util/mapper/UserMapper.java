package ru.aston.intensive.util.mapper;

import ru.aston.intensive.dto.UserRequestDto;
import ru.aston.intensive.dto.UserResponseDto;
import ru.aston.intensive.entity.UserEntity;

public class UserMapper {

    public static UserEntity dtoToEntity(UserRequestDto dto) {
        return UserEntity.builder()
                .name(dto.name())
                .email(dto.email())
                .age(dto.age())
                .build();
    }

    public static UserResponseDto entityToDto(UserEntity entity) {
        return new UserResponseDto(entity.getName(), entity.getEmail(), entity.getAge());
    }
}
