package com.example.clinic.service;

import com.example.clinic.model.Person;
import com.example.clinic.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public Person updateLastLogin(long id) {
        personRepository.updateLastLoginDate(id);

        // Read the person from the database to get the latest "last login date"
        return personRepository.findById(id).orElseThrow();
    }
}