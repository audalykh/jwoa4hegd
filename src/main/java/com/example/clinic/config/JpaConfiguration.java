package com.example.clinic.config;

import com.example.clinic.model.AuditableEntity;
import com.example.clinic.repository.ExtendedJpaRepositoryImpl;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
    public AuditorAware<AuditableEntity> auditorProvider() {
        return new JpaAuditorProvider();
    }

    @RequiredArgsConstructor
    private static class JpaAuditorProvider implements AuditorAware<AuditableEntity> {

        @Override
        public Optional<AuditableEntity> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            return Optional.ofNullable((AuditableEntity) authentication.getPrincipal());
//            return Optional.ofNullable(SecurityContextHolder.getContext())
//                    .map(SecurityContext::getAuthentication)
//                    .map(Principal::getName)
//                    .map(this::getUserInfo);
        }
    }
}