package ru.aston.intensive.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.aston.intensive.dto.UserDto;
import ru.aston.intensive.entity.UserEntity;
import ru.aston.intensive.service.UserService;
import ru.aston.intensive.util.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/api/user-service/users/")
@Log4j2
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream()
                .map(UserMapper::entityToDto)
                .toList());
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<UserDto> find(@PathVariable(name = "id") Long id) {
        return ResponseEntity
                .ok(UserMapper.entityToDto(userService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto userDto) {
        UserEntity userEntity = UserMapper.dtoToEntity(userDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserMapper.entityToDto(userService.create(userEntity)));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<UserDto> update(@PathVariable(name = "id") Long id,
                                          @Valid @RequestBody UserDto userDto) {
        UserEntity userEntity = UserMapper.dtoToEntity(userDto);
        return ResponseEntity
                .ok(UserMapper.entityToDto(userService.update(id, userEntity)));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> delete(@PathVariable(name = "id") Long id) {

        userService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Successfully deleted user with id = " + id);
    }
}
