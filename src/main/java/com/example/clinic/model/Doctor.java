package com.example.clinic.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.hibernate.annotations.SQLDelete;

import static com.example.clinic.model.Person.SQL_DELETE;

@Entity
@DiscriminatorValue("D")
@SQLDelete(sql = SQL_DELETE)
public class Doctor extends Person {
}