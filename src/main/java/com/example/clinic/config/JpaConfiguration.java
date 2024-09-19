package com.example.clinic.config;

import com.example.clinic.model.Person;
import com.example.clinic.repository.ExtendedJpaRepositoryImpl;
import com.example.clinic.security.CustomUsernamePasswordAuthenticationToken;
import com.example.clinic.service.DoctorService;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.clinic.repository",
        repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfiguration {

    @Bean
    public AuditorAware<Long> auditorProvider(DoctorService doctorService) {
        return new JpaAuditorProvider(doctorService);
    }

    private record JpaAuditorProvider(DoctorService doctorService) implements AuditorAware<Long> {

        @Override
        public Optional<Long> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            if (authentication instanceof CustomUsernamePasswordAuthenticationToken token) {
                return Optional.of(token.getActorId());
            } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
                return doctorService.findByEmail(authentication.getName())
                        .map(Person::getId);
            }

            return Optional.empty();
        }
    }
}