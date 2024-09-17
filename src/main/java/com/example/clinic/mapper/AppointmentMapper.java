package com.example.clinic.mapper;

import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.dto.AppointmentRequestDto;
import com.example.clinic.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = PersonMapper.class
)
public interface AppointmentMapper {

    AppointmentDto toDto(Appointment entity);

    Appointment toEntity(AppointmentRequestDto dto);
}