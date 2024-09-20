package com.example.clinic.config;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * This class provides configuration for reports in the application.
 */
@Configuration
@RequiredArgsConstructor
public class ReportsConfiguration {

    @Value("classpath:templates/reports/tests.jrxml")
    private Resource testsTemplateResource;

    /**
     * This method compiles a JasperReports template file into a JasperReport object.
     * As long as JasperReport is thread-safe we can have it as a singleton Spring bean.
     */
    @Bean
    public JasperReport testsReport() throws IOException, JRException {
        try (var inputStream = testsTemplateResource.getInputStream()) {
            return JasperCompileManager.compileReport(inputStream);
        }
    }
}