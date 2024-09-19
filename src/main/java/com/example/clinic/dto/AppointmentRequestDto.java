package com.example.clinic.dto;

import com.example.clinic.model.AppointmentStatus;
import com.example.clinic.validation.RevisitTimeValid;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The appointment update request does not have "patientId" field because the appointment is not supposed to be
 * re-assigned to another patient.
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDto {

    private AppointmentStatus status;

    @RevisitTimeValid
    private LocalDateTime revisitDateTime;
}
