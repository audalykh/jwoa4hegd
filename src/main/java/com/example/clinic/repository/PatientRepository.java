package com.example.clinic.repository;

import com.example.clinic.model.Patient;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends BasePersonRepository<Patient>  {

    boolean existsByEmail(String email);
}