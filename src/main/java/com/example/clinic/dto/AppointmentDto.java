package com.example.clinic.dto;

import com.example.clinic.model.AppointmentStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentDto {

    private long id;

    private PersonDto patient;
    private AppointmentStatus status;
    private LocalDateTime revisitDateTime;
    // TODO: tests

    private PersonDto createdBy;
    private PersonDto updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
