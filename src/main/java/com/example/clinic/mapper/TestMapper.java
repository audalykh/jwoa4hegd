package com.example.clinic.mapper;

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
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = IGNORE
)
public interface TestMapper {

    @Mapping(target = "result", expression =
            "java(dto.getResult() != null ? dto.getResult() : com.example.clinic.model.TestResult.UNKNOWN)")
    Test toEntity(TestCreateDto dto);

    Test toEntity(TestDto dto);

    Test toEntity(TestDto dto, @MappingTarget Test test);

    TestDto toDto(Test entity);
}