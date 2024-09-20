package com.example.clinic.security;

import com.example.clinic.model.Doctor;
import com.example.clinic.model.Person;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The Role class represents a role that a person can have in the system.
 * It is an enumeration type that contains predefined roles such as DOCTOR and PATIENT.
 * Each role has a corresponding role class and a role name.
 */
@RequiredArgsConstructor
public enum Role {
    DOCTOR(Doctor.class, Auth.ROLE_DOCTOR),
    PATIENT(Person.class, Auth.ROLE_PATIENT);

    private final Class<? extends Person> roleClass;
    @Getter
    private final String roleName;

    public static Role fromPerson(Person person) {
        for (Role role : values()) {
            if (role.roleClass.isInstance(person)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown person type: " + person.getClass());
    }
}