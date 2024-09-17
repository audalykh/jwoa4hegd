package com.example.clinic.config;

import com.example.clinic.repository.ExtendedJpaRepositoryImpl;
import com.example.clinic.security.CustomUsernamePasswordAuthenticationToken;
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
    public AuditorAware<Long> auditorProvider() {
        return new JpaAuditorProvider();
    }

    private record JpaAuditorProvider() implements AuditorAware<Long> {

        @Override
        public Optional<Long> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            if (authentication instanceof CustomUsernamePasswordAuthenticationToken token) {
                return Optional.of(token.getActorId());
            }

            return Optional.empty();
        }
    }
}