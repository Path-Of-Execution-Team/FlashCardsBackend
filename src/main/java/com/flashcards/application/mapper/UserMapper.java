package com.flashcards.application.mapper;

import com.flashcards.application.dto.UserCreationDto;
import com.flashcards.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "password", target = "passwordHash")
    User toEntity(UserCreationDto userCreationDto);

    @Mapping(source = "passwordHash", target = "password")
    UserCreationDto toDto(User user);
}
