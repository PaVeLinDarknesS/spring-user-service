package ru.aston.intensive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.aston.intensive.dto.UserRequestDto;
import ru.aston.intensive.dto.UserResponseDto;
import ru.aston.intensive.entity.UserEntity;
import ru.aston.intensive.exception.ApiError;
import ru.aston.intensive.service.UserService;
import ru.aston.intensive.util.assembler.UserEntityToModelAssembler;
import ru.aston.intensive.util.mapper.UserMapper;

import java.util.List;

@RestController
@RequestMapping("/api/user-service/users/")
@Log4j2
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
@Validated
public class UserController {

    private final UserService userService;
    private final UserEntityToModelAssembler userEntityToModelAssembler;

    @Operation(
            summary = "Find users",
            description = "Find all users"
    )
    @Tag(name = "Get")
    @GetMapping
    public ResponseEntity<List<EntityModel<UserResponseDto>>> findAll() {
        return ResponseEntity.ok(userService.findAll().stream()
                .map(userEntityToModelAssembler::toModel)
                .toList());
    }

    @Operation(
            summary = "Find user",
            description = "Find user by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found successfully",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = "application+json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found by Id",
                            content = @Content(
                                    mediaType = "application+json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @Tag(name = "Get")
    @GetMapping(path = "{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> find(
            @PathVariable(name = "id")
            @Min(value = 1, message = "ID must be at least 1")
            @Parameter(description = "User ID", example = "1", required = true)
            Long id) {

        return ResponseEntity
                .ok(userEntityToModelAssembler.toModel(userService.findById(id)));
    }

    @Operation(
            summary = "Create user",
            description = "Create new user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = "application+json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User with this Email already exist",
                            content = @Content(
                                    mediaType = "application+json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDto>> create(
            @Valid
            @RequestBody
            @Parameter(
                    description = "User data for creation",
                    required = true,
                    example = "{\"username\": \"john_doe\", \"email\": \"john@example.com\"\", \"age\": \"20\"}"
            )
            UserRequestDto userRequestDto) {
        UserEntity userEntity = UserMapper.dtoToEntity(userRequestDto);
        userEntity = userService.create(userEntity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userEntityToModelAssembler.toModel(userEntity));
    }

    @Operation(
            summary = "Update user",
            description = "Update the existing user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User update successfully",
                            content = @Content(
                                    mediaType = "application/hal+json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = "application+json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found by Id",
                            content = @Content(
                                    mediaType = "application+json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User with this Email already exist",
                            content = @Content(
                                    mediaType = "application+json",
                                    schema = @Schema(implementation = ApiError.class)
                            )

                    )
            }
    )
    @PutMapping(path = "{id}")
    public ResponseEntity<EntityModel<UserResponseDto>> update(
            @PathVariable(name = "id")
            @Min(value = 1, message = "ID must be at least 1")
            @Parameter(description = "User ID", example = "1", required = true)
            Long id,
            @Parameter(
                    description = "User data for creation",
                    required = true,
                    example = "{\"username\": \"john_doe\", \"email\": \"john@example.com\"\", \"age\": \"20\"}"
            )
            @Valid @RequestBody UserRequestDto userRequestDto) {
        UserEntity userEntity = UserMapper.dtoToEntity(userRequestDto);
        return ResponseEntity
                .ok(userEntityToModelAssembler.toModel(userService.update(id, userEntity)));
    }

    @Operation(
            summary = "Delete user",
            description = "Delete the existing user",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found by Id",
                            content = @Content(
                                    mediaType = "application+json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> delete(
            @PathVariable(name = "id")
            @Min(value = 1, message = "ID must be at least 1")
            @Parameter(description = "User ID", example = "1", required = true)
            Long id) {

        userService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Successfully deleted user with id = " + id);
    }
}
