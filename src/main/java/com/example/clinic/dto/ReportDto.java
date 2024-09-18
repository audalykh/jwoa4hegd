package com.example.clinic.dto;

import com.example.clinic.model.Clinic;
import com.example.clinic.model.Patient;
import java.util.List;

public record ReportDto(List<TestDto> tests, Patient patient, Clinic clinic) {
}