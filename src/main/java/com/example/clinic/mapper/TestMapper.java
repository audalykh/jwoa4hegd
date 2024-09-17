package com.example.clinic.mapper;

import com.example.clinic.dto.TestBaseDto;
import com.example.clinic.dto.TestCreateDto;
import com.example.clinic.dto.TestDto;
import com.example.clinic.model.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TestMapper {

    Test toEntity(TestCreateDto dto);

    Test toEntity(TestDto dto);

    @Mapping(target = "result", nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "resultDateTime", nullValuePropertyMappingStrategy = IGNORE)
    Test toEntity(TestBaseDto dto, @MappingTarget Test test);
}