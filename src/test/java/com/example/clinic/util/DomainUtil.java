package com.example.clinic.util;

import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.dto.TestDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.Doctor;
import com.example.clinic.model.EntityType;
import com.example.clinic.model.Log;
import com.example.clinic.model.Patient;
import com.example.clinic.service.AppointmentService;
import com.example.clinic.service.DoctorService;
import com.example.clinic.service.LogService;
import com.example.clinic.service.PatientService;
import com.example.clinic.service.TestService;
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
    private AppointmentService appointmentService;

    @Autowired
    private TestService testService;

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

    public Log getLogByTypes(ActionType type, EntityType entityType) {
        return logService.getAll().stream()
                .filter(log -> log.getActionType() == type && log.getEntityType() == entityType)
                .findFirst().orElse(null);
    }

    public List<AppointmentDto> getAllAppointments() {
        return appointmentService.getPage(Pageable.unpaged()).getContent();
    }

    public List<TestDto> getPatientTests(Patient patient) {
        return testService.getPatientTests(patient);
    }

    public List<TestDto> getAllTests() {
        return testService.getAllTests();
    }
}