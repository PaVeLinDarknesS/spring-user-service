package ru.aston.intensive.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.aston.intensive.dto.UserRequestDto;
import ru.aston.intensive.dto.UserResponseDto;
import ru.aston.intensive.exception.ApiError;

import java.util.List;

@Tag(name = "User Management", description = "APIs for managing users")
public interface UserController {

    @Operation(
            summary = "Find users",
            description = "Find all users"
    )
    @Tag(name = "Get")
    public ResponseEntity<List<EntityModel<UserResponseDto>>> findAll();

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
    public ResponseEntity<EntityModel<UserResponseDto>> find(
            @PathVariable
            Long id);

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
    public ResponseEntity<EntityModel<UserResponseDto>> create(
            @RequestBody
            UserRequestDto userRequestDto);

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
    public ResponseEntity<EntityModel<UserResponseDto>> update(
            @PathVariable
            Long id,
            @RequestBody
            UserRequestDto userRequestDto);

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
    ResponseEntity<String> delete(
            @PathVariable
            Long id);
}
