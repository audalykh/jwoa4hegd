package com.example.clinic.mapper;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.model.Doctor;
import com.example.clinic.model.Patient;
import com.example.clinic.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class PersonMapperDecorator implements PersonMapper {

    @Autowired
    @Qualifier("delegate")
    private PersonMapper delegate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Doctor toDoctorEntity(PersonBaseDto dto) {
        var entity = delegate.toDoctorEntity(dto);
        return initPassword(entity, dto);
    }

    @Override
    public Patient toPatientEntity(PersonBaseDto dto) {
        var entity = delegate.toPatientEntity(dto);
        return initPassword(entity, dto);
    }

    @Override
    public Person toEntity(PersonBaseDto dto, Person entity) {
        return initPassword(delegate.toEntity(dto, entity), dto);
    }

    /**
     * Initializes the password of a person entity with the provided password from the DTO on person create or update.
     */
    private <T extends Person> T initPassword(T entity, PersonBaseDto dto) {
        if (entity != null) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return entity;
    }
}
