package com.example.clinic.validation;

import com.example.clinic.service.ClinicService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * This class is a validator for the RevisitTimeValid annotation.
 * It checks if the given LocalDateTime value is within the interval of the clinic's working hours.
 */
@Component
@RequiredArgsConstructor
public class RevisitTimeValidator implements ConstraintValidator<RevisitTimeValid, LocalDateTime> {

    private final ClinicService clinicService;

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value != null) {
            var clinic = clinicService.getClinic();

            int valueHour = value.getHour();
            return valueHour >= clinic.getFromHour() && valueHour < clinic.getToHour();
        }
        return true;
    }
}
