package com.example.clinic.dto;

import com.example.clinic.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AppointmentRequestDto {

    @NotNull
    private Long patientId;

    private AppointmentStatus status;

    private LocalDateTime revisitDateTime;
}
