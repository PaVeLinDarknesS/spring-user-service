package ru.aston.intensive.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.intensive.dto.UserRequestDto;
import ru.aston.intensive.dto.UserResponseDto;
import ru.aston.intensive.entity.UserEntity;
import ru.aston.intensive.service.UserService;
import ru.aston.intensive.util.assembler.UserEntityToModelAssembler;
import ru.aston.intensive.util.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/api/users/")
@Log4j2
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;
    private final UserEntityToModelAssembler userEntityToModelAssembler;

    @GetMapping
    public ResponseEntity<List<EntityModel<UserResponseDto>>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream()
                .map(userEntityToModelAssembler::toModel)
                .toList());
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> find(Long id) {

        return ResponseEntity
                .ok(userEntityToModelAssembler.toModel(userService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDto>> create(@Valid UserRequestDto userRequestDto) {
        UserEntity userEntity = UserMapper.dtoToEntity(userRequestDto);
        userEntity = userService.create(userEntity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userEntityToModelAssembler.toModel(userEntity));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> update(Long id,
                                                               @Valid UserRequestDto userRequestDto) {
        UserEntity userEntity = UserMapper.dtoToEntity(userRequestDto);
        return ResponseEntity
                .ok(userEntityToModelAssembler.toModel(userService.update(id, userEntity)));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> delete(Long id) {

        userService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Successfully deleted user with id = " + id);
    }
}
