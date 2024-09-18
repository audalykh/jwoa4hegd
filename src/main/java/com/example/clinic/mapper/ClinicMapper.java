package com.example.clinic.mapper;

import com.example.clinic.dto.ClinicBaseDto;
import com.example.clinic.dto.ClinicDto;
import com.example.clinic.model.Clinic;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = LogoMapper.class)
public interface ClinicMapper {

    Clinic toEntity(ClinicBaseDto dto);

    ClinicDto toDto(Clinic clinic);

    Clinic toEntity(ClinicBaseDto dto, @MappingTarget Clinic entity);
}
