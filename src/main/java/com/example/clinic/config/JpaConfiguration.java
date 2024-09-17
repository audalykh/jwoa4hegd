package com.example.clinic.config;

import com.example.clinic.model.Doctor;
import com.example.clinic.repository.ExtendedJpaRepositoryImpl;
import com.example.clinic.service.DoctorService;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.clinic.repository",
        repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfiguration {

    @Bean
    public AuditorAware<Doctor> auditorProvider(DoctorService doctorService) {
        return new JpaAuditorProvider(doctorService);
    }

    private record JpaAuditorProvider(DoctorService doctorService) implements AuditorAware<Doctor> {

        @Override
        public Optional<Doctor> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            if (authentication.getPrincipal() instanceof String stringPrincipal) {
                return doctorService.findByEmail(stringPrincipal);
            }

            return Optional.empty();
        }
    }
}