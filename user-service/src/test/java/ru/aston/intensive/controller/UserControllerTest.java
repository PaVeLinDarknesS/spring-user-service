package ru.aston.intensive.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.aston.intensive.dto.UserRequestDto;
import ru.aston.intensive.dto.UserResponseDto;
import ru.aston.intensive.entity.UserEntity;
import ru.aston.intensive.exception.EmailExistingException;
import ru.aston.intensive.exception.UserNotFoundException;
import ru.aston.intensive.service.UserService;
import ru.aston.intensive.util.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String URI_START = "/api/user-service/users/";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll_whenOk() throws Exception {
        List<UserEntity> userEntityList = List.of(
                UserEntity.builder()
                        .name("Vasua")
                        .email("vasua@test.org")
                        .age(30)
                        .build(),
                UserEntity.builder()
                        .name("Petua")
                        .email("petya@petya.org")
                        .age(22)
                        .build()
        );

        BDDMockito.given(userService.findAll())
                .willReturn(userEntityList);

        mockMvc.perform(get(URI_START))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Vasua"))
                .andExpect(jsonPath("$[1].name").value("Petua"));
    }

    @Test
    void find_whenIdExist_userDto() throws Exception {
        UserEntity existUser = getExistUser();

        BDDMockito.given(userService.findById(existUser.getId()))
                .willReturn(existUser);

        mockMvc.perform(get(URI_START + existUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(existUser.getName()))
                .andExpect(jsonPath("$.email").value(existUser.getEmail()))
                .andExpect(jsonPath("$.age").value(existUser.getAge()));
    }

    @Test
    void create_whenOk() throws Exception {
        UserRequestDto notExistUser = getNotExistUser();

        UserEntity expectedUser = UserMapper.dtoToEntity(notExistUser);
        expectedUser.setId(1L);

        BDDMockito.given(userService.create(any(UserEntity.class)))
                .willReturn(expectedUser);

        mockMvc.perform(post(URI_START)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notExistUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(notExistUser.name()))
                .andExpect(jsonPath("$.email").value(notExistUser.email()))
                .andExpect(jsonPath("$.age").value(notExistUser.age()));

        verify(userService).create(any(UserEntity.class));
    }

    @Test
    void create_whenEmailIsExist_status409Conflict() throws Exception {
        UserRequestDto notExistUser = getNotExistUser();

        UserEntity expectedUser = UserMapper.dtoToEntity(notExistUser);
        expectedUser.setId(1L);

        BDDMockito.given(userService.create(any(UserEntity.class)))
                .willThrow(new EmailExistingException("User already exist with email = " + notExistUser.email()));

        mockMvc.perform(post(URI_START)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notExistUser)))
                .andExpect(status().isConflict());

        verify(userService).create(any(UserEntity.class));
    }

    @Test
    void update_whenOk() throws Exception {
        UserEntity updatedUser = getExistUser();
        UserResponseDto sendUser = UserMapper.entityToDto(updatedUser);

        BDDMockito.given(userService.update(anyLong(), any(UserEntity.class)))
                .willReturn(updatedUser);

        mockMvc.perform(put(URI_START + updatedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sendUser.name()))
                .andExpect(jsonPath("$.email").value(sendUser.email()))
                .andExpect(jsonPath("$.age").value(sendUser.age()));

        verify(userService).update(anyLong(), any(UserEntity.class));
    }

    @Test
    void update_whenSendUserIncorrect_status400BadRequestAndErrors() throws Exception {
        UserEntity invalidUser = new UserEntity(1L, "a", "invalidEmail", 1, LocalDateTime.now());
        UserResponseDto sendUser = UserMapper.entityToDto(invalidUser);

        mockMvc.perform(put(URI_START + invalidUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name", matchesPattern(".+")))
                .andExpect(jsonPath("$.email", matchesPattern(".+")))
                .andExpect(jsonPath("$.age", matchesPattern(".+")));

        verify(userService, never()).update(anyLong(), any(UserEntity.class));
    }

    @Test
    void delete_whenOk() throws Exception {
        UserEntity deletedUser = getExistUser();

        BDDMockito.willDoNothing().given(userService).delete(any(Long.class));

        mockMvc.perform(delete(URI_START + deletedUser.getId()))
                .andExpect(status().isNoContent());

        verify(userService).delete(deletedUser.getId());
    }

    @Test
    void delete_whenIdNotExist_status404NotFound() throws Exception {
        UserEntity deletedUser = getExistUser();

        BDDMockito.doThrow(new UserNotFoundException("User not exist with id = " + deletedUser.getId()))
                .when(userService).delete(any(Long.class));

        mockMvc.perform(delete(URI_START + deletedUser.getId()))
                .andExpect(status().isNotFound());

        verify(userService).delete(deletedUser.getId());
    }

    private UserEntity getExistUser() {
        return UserEntity.builder()
                .id(1L)
                .name("Vasua")
                .email("vasua@test.org")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private UserRequestDto getNotExistUser() {
        return new UserRequestDto("Anya", "anya@anya.org", 80);
    }
}
