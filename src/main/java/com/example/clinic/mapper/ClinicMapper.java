package com.example.clinic.mapper;

import com.example.clinic.dto.ClinicBaseDto;
import com.example.clinic.model.Clinic;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ClinicMapper {

    Clinic toEntity(ClinicBaseDto dto);

    Clinic toEntity(ClinicBaseDto dto, @MappingTarget Clinic entity);
}
