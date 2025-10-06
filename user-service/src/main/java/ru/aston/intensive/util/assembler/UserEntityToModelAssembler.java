package ru.aston.intensive.util.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.aston.intensive.controller.UserControllerImpl;
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
                linkTo(methodOn(UserControllerImpl.class).find(entity.getId())).withSelfRel(),
                linkTo(methodOn(UserControllerImpl.class).create(null)).withRel("create"),
                linkTo(methodOn(UserControllerImpl.class).update(entity.getId(), null)).withRel("update"),
                linkTo(methodOn(UserControllerImpl.class).delete(entity.getId())).withRel("delete"),
                linkTo(methodOn(UserControllerImpl.class).findAll()).withRel(IanaLinkRelations.COLLECTION));
    }
}
