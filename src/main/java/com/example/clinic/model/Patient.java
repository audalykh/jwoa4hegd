package com.example.clinic.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.io.Serial;

@Entity
@DiscriminatorValue("P")
public class Patient extends Person {
    @Serial
    private static final long serialVersionUID = -2236045265614268224L;
}