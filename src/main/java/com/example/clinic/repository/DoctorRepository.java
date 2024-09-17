package com.example.clinic.repository;

import com.example.clinic.model.Doctor;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends CustomRepository<Doctor, Long> {

    Optional<Doctor> findDoctorByEmail(String email);

    boolean existsByEmail(String email);
}