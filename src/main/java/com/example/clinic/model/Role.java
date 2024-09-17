package com.example.clinic.model;

import com.example.clinic.config.Auth;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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