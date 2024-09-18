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

@Configuration
@RequiredArgsConstructor
public class ReportsConfiguration {

    @Value("classpath:reports/tests.jrxml")
    private Resource testsTemplateResource;

    @Bean
    public JasperReport testsReport() throws IOException, JRException {
        try (var inputStream = testsTemplateResource.getInputStream()) {
            return JasperCompileManager.compileReport(inputStream);
        }
    }
}