package com.example.clinic.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class RevisitTimeValidator implements ConstraintValidator<RevisitTimeValid, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        // TODO
        return true;
    }
}
