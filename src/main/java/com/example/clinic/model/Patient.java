package com.example.clinic.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("P")
public class Patient extends Person {
}