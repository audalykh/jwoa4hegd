package com.example.clinic.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import static com.example.clinic.model.Person.SQL_DELETE;

@Getter
@Setter
@Entity
@DiscriminatorValue("P")
@SQLDelete(sql = SQL_DELETE)
public class Patient extends Person {

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments;
}