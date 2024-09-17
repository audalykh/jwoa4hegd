package com.example.clinic.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    ROLE_DOCTOR(Doctor.class),
    ROLE_PATIENT(Person.class);

    private final Class<? extends Person> roleClass;

    public static Role fromPerson(Person person) {
        for (Role role : values()) {
            if (role.roleClass.isInstance(person)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown person type: " + person.getClass());
    }
}