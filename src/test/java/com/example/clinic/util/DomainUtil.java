package com.example.clinic.util;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.Doctor;
import com.example.clinic.model.Log;
import com.example.clinic.model.Patient;
import com.example.clinic.service.DoctorService;
import com.example.clinic.service.LogService;
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

    @Autowired
    private LogService logService;

    public Patient createPatient(PersonBaseDto patient) {
        return patientService.createOrThrow(patient);
    }

    public Doctor createDoctor(PersonBaseDto doctor) {
        return doctorService.createOrThrow(doctor);
    }

    public Doctor getDoctorByEmail(String email) {
        return doctorService.findByEmailOrThrow(email);
    }

    public List<PersonDto> getAllDoctors() {
        return doctorService.getPage(Pageable.unpaged()).getContent();
    }

    public List<PersonDto> getAllPatients() {
        return patientService.getPage(Pageable.unpaged()).getContent();
    }

    public List<Log> getAllLogs() {
        return logService.getAll();
    }

    public Log getLogByType(ActionType type) {
        return logService.getAll().stream().filter(log -> log.getActionType() == type).findFirst().orElse(null);
    }
}