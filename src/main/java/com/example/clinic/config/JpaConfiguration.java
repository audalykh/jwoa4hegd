package com.example.clinic.config;

import com.example.clinic.repository.ExtendedJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.clinic.repository",
        repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfiguration {
}