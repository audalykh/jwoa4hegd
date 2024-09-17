package com.example.clinic.dto;

import com.example.clinic.model.AppointmentStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The appointment update request does not have "patientId" field because the appointment is not supposed to be
 * re-assigned to another patient.
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppointmentRequestDto {

    private AppointmentStatus status;

    private LocalDateTime revisitDateTime;
}
