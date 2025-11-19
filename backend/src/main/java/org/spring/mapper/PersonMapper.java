package org.spring.mapper;

import org.mapstruct.Mapper;
import org.spring.dto.PersonDto;
import org.spring.model.PersonEntity;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonDto toDto (PersonEntity entity);

    PersonEntity toEntity (PersonDto dto);
}
