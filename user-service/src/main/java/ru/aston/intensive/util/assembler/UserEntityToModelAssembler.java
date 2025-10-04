package ru.aston.intensive.util.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.aston.intensive.controller.UserController;
import ru.aston.intensive.dto.UserResponseDto;
import ru.aston.intensive.entity.UserEntity;
import ru.aston.intensive.util.mapper.UserMapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserEntityToModelAssembler implements
        RepresentationModelAssembler<UserEntity, EntityModel<UserResponseDto>> {

    @Override
    public EntityModel<UserResponseDto> toModel(UserEntity entity) {

        return EntityModel.of(UserMapper.entityToDto(entity),
                linkTo(methodOn(UserController.class).find(entity.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).create(null)).withRel("create"),
                linkTo(methodOn(UserController.class).update(entity.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(entity.getId())).withRel("delete"),
                linkTo(methodOn(UserController.class).findAll()).withRel(IanaLinkRelations.COLLECTION));
    }
}
