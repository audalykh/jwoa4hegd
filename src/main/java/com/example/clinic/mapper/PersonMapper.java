package com.example.clinic.mapper;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.model.Doctor;
import com.example.clinic.model.Patient;
import com.example.clinic.model.Person;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
@DecoratedWith(PersonMapperDecorator.class)
public interface PersonMapper {

    @Mapping(target = "password", ignore = true)
    Doctor toDoctorEntity(PersonBaseDto dto);

    @Mapping(target = "password", ignore = true)
    Patient toPatientEntity(PersonBaseDto dto);

    @Mapping(target = "password", ignore = true)
    PersonDto toDto(Person entity);

    @Mapping(target = "password", ignore = true)
    Patient toEntity(PersonBaseDto dto, @MappingTarget Patient entity);
}