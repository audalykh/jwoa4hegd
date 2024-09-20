package com.example.clinic.config;

import com.example.clinic.jpa.DefaultJpaAuditorProvider;
import com.example.clinic.repository.ExtendedJpaRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for JPA related setup.
 * This class is responsible for configuring JPA repositories, auditing, and providing auditor information.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.clinic.repository",
        repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfiguration {

    /**
     * Creates the {@link AuditorAware} implementation that provides the auditor information for JPA auditing.
     */
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new DefaultJpaAuditorProvider();
    }
}