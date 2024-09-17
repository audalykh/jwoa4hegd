package com.example.clinic.mapper;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.model.Doctor;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
@DecoratedWith(PersonMapperDecorator.class)
public interface PersonMapper {

    @Mapping(target = "password", ignore = true)
    Doctor toDoctorEntity(PersonBaseDto dto);

    @Mapping(target = "password", ignore = true)
    PersonDto toDto(Doctor entity);
}