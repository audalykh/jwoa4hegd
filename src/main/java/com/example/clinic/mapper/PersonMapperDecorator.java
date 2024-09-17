package com.example.clinic.mapper;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.model.Doctor;
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
        if (entity != null) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return entity;
    }
}

