package com.example.clinic.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.io.Serial;

@Entity
@DiscriminatorValue("D")
public class Doctor extends Person {
    @Serial
    private static final long serialVersionUID = -4491170681600833851L;
}