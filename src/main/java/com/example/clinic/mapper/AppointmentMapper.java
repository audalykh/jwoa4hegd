package com.example.clinic.mapper;

import com.example.clinic.dto.AppointmentCreateDto;
import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.dto.AppointmentRequestDto;
import com.example.clinic.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = PersonMapper.class)
public interface AppointmentMapper {

    AppointmentDto toDto(Appointment entity);

    /**
     * For newly created appointment the status should always be NEW
     */
    @Mapping(target = "status", constant = "NEW")
    Appointment toEntity(AppointmentCreateDto dto);

    @Mapping(target = "status", nullValuePropertyMappingStrategy = IGNORE)
    Appointment toEntity(AppointmentRequestDto dto, @MappingTarget Appointment entity);
}