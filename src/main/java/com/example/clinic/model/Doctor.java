package com.example.clinic.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("D")
public class Doctor extends Person {
}