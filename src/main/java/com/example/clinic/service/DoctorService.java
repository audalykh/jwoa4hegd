package com.example.clinic.service;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.exception.DomainObjectNotFoundException;
import com.example.clinic.exception.PersonAlreadyExistException;
import com.example.clinic.mapper.PersonMapper;
import com.example.clinic.model.Doctor;
import com.example.clinic.repository.DoctorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("doctorService")
public class DoctorService extends BasePersonService<Doctor, DoctorRepository> {

    public DoctorService(DoctorRepository repository, PersonMapper personMapper) {
        super(repository, personMapper);
    }

    @Transactional
    public PersonDto create(PersonBaseDto dto) throws PersonAlreadyExistException {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new PersonAlreadyExistException("Doctor with email <" + dto.getEmail() + "> already exists");
        } else {
            // Save and refresh the entity to get the created date
            var doctor = repository.saveAndRefresh(personMapper.toDoctorEntity(dto));
            return personMapper.toDto(doctor);
        }
    }

    /**
     * Creates a new doctor or throws an exception if the doctor already exists.
     */
    @Transactional
    public Doctor createOrThrow(PersonBaseDto dto) {
        return repository.save(personMapper.toDoctorEntity(dto));
    }

    @Transactional(readOnly = true)
    public Doctor findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Doctor not found for email: " + email)
        );
    }

    @Transactional(readOnly = true)
    public PersonDto getById(Long id) {
        return repository.findById(id)
                .map(personMapper::toDto)
                .orElseThrow(() -> new DomainObjectNotFoundException("Doctor not found for id: " + id));
    }

    @Transactional
    public PersonDto update(Long id, PersonBaseDto dto) {
        var doctor = repository.findById(id)
                .orElseThrow(() -> new DomainObjectNotFoundException("Doctor not found for id: " + id));
        return personMapper.toDto(personMapper.toEntity(dto, doctor));
    }
}