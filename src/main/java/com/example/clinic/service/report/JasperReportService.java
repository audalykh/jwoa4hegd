package com.example.clinic.service.report;

import com.example.clinic.dto.ReportDto;
import com.example.clinic.exception.ReportGenerationException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class JasperReportService implements ReportService {

    private final JasperReport testsReport;

    @Override
    public byte[] generateReport(ReportDto reportDto) {
        try {
            JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(reportDto.tests());
            var clinic = reportDto.clinic();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("clinicName", clinic.getName());
            parameters.put("clinicEmail", clinic.getAddress());
            parameters.put("clinicPhone", clinic.getPhone());
            parameters.put("fullName", reportDto.patient().getFullName());
            parameters.put("logoImage", clinic.getLogo().getData());

            JasperPrint jasperPrint = JasperFillManager.fillReport(testsReport, parameters, datasource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new ReportGenerationException("Error while generating report", e);
        }
    }
}