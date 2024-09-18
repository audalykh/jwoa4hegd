package com.example.clinic.service.report;

import com.example.clinic.dto.ReportDto;
import com.example.clinic.exception.ReportGenerationException;

public interface ReportService {

    /**
     * Generates a report in PDF format based on the provided {@link ReportDto}.
     *
     * @param reportDto The {@link ReportDto} containing the necessary data for generating the report.
     * @return The report as a byte array in PDF format.
     * @throws ReportGenerationException if an error occurs during the report generation process.
     */
    byte[] generateReport(ReportDto reportDto);
}