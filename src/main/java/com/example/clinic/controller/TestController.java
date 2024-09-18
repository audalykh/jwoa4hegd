package com.example.clinic.controller;

import com.example.clinic.dto.ReportDto;
import com.example.clinic.dto.TestBaseDto;
import com.example.clinic.dto.TestCreateDto;
import com.example.clinic.dto.TestDto;
import com.example.clinic.model.Patient;
import com.example.clinic.service.ClinicService;
import com.example.clinic.service.PatientService;
import com.example.clinic.service.TestService;
import com.example.clinic.service.report.ReportService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.example.clinic.security.Auth.ROLE_DOCTOR;
import static com.example.clinic.security.Auth.ROLE_PATIENT;

@RestController
@Secured(ROLE_DOCTOR)
@RequestMapping("/tests")
@RequiredArgsConstructor
public class TestController {

    private static final String PDF_REPORT_FILENAME = "tests_report.pdf";

    private final TestService testService;
    private final ClinicService clinicService;
    private final PatientService patientService;
    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid TestCreateDto dto) {
        testService.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody @Valid TestBaseDto dto) {
        testService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        testService.delete(id);
    }

    /**
     * Retrieves the list of medical tests taken by a specific patient.
     * The list includes all tests taken by the patient from all the visits.
     * The list is sorted in ascending order based on the test submission time.
     *
     * @param principal the principal object representing the authenticated patient
     * @return the list of medical tests taken by the patient
     */
    @GetMapping
    @Secured(ROLE_PATIENT)
    public List<TestDto> getPatientTests(Principal principal) {
        Patient patient = patientService.findByEmailOrThrow(principal.getName());
        return testService.getPatientTests(patient);
    }

    /**
     * Generates a PDF report containing all the medical tests taken by the patient.
     */
    @Secured(ROLE_PATIENT)
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateReport(Principal principal) {
        Patient patient = patientService.findByEmailOrThrow(principal.getName());
        byte[] bytes = reportService.generateReport(new ReportDto(
                testService.getPatientTests(patient),
                patient,
                clinicService.getOneOrThrow()
        ));

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + PDF_REPORT_FILENAME)
                .body(bytes);
    }
}