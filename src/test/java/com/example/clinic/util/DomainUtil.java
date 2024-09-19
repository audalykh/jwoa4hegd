package com.example.clinic.util;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.model.Doctor;
import com.example.clinic.model.Patient;
import com.example.clinic.service.DoctorService;
import com.example.clinic.service.PatientService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class DomainUtil {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    public Patient createPatient(PersonBaseDto patient) {
        return patientService.createOrThrow(patient);
    }

    public Doctor createDoctor(PersonBaseDto doctor) {
        return doctorService.createOrThrow(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorService.getPage(Pageable.unpaged()).getContent();
    }
}