package com.example.clinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This class represents the configuration for the application.
 * It enables scheduling using the {@link EnableScheduling} annotation.
 * Additionally, it enables AspectJ auto-proxying using the {@link EnableAspectJAutoProxy} annotation.
 * <p>
 * This class does not contain any methods or fields, it only serves as a marker for configuration purposes.
 */
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
public class ApplicationConfiguration {
}