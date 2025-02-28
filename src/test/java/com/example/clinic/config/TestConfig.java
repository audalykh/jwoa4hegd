package com.example.clinic.config;

import com.example.clinic.util.DbUtil;
import com.example.clinic.util.TestJpaAuditorProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;

/**
 * This class is a configuration class for testing purposes.
 * It provides a bean for the auditorProvider with primary annotation.
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public AuditorAware<Long> auditorProvider(DbUtil dbUtil) {
        return new TestJpaAuditorProvider(dbUtil);
    }
}