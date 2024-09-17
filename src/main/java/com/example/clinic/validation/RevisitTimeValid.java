package com.example.clinic.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RevisitTimeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RevisitTimeValid {

    String message() default "Invalid revisit time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
