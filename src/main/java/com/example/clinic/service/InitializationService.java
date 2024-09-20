package com.example.clinic.service;

import com.example.clinic.config.InitDataConfiguration;
import com.example.clinic.dto.ClinicBaseDto;
import com.example.clinic.dto.LogoResourceDto;
import com.example.clinic.dto.PersonBaseDto;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * This class provides functionality for initializing the application on startup.
 * It creates an admin doctor and a default clinic if necessary.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitializationService {

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final InitDataConfiguration dataConfiguration;
    private final ResourceLoader resourceLoader;

    @EventListener(ContextRefreshedEvent.class)
    public void onStartupEvent() {
        initAdminDoctor();
        if (dataConfiguration.getClinicDefault().isAutoCreate()) {
            initClinic();
        }
    }

    private void initAdminDoctor() {
        log.trace("Checking if an admin doctor should be created on application startup");

        var email = dataConfiguration.getAdminDoctor().getEmail();
        Assert.isTrue(EMAIL_VALIDATOR.isValid(email),
                "Invalid email address provided for admin doctor: " + email);

        var adminDoctor = doctorService.findByEmail(email);
        adminDoctor.ifPresentOrElse(
                doctor -> log.trace("Admin doctor found; skipping creation..."),
                this::createAdminDoctor);
    }

    private void createAdminDoctor() {
        log.info("Admin doctor not found, creating a new one");

        var doctor = dataConfiguration.getAdminDoctor();
        doctorService.createOrThrow(new PersonBaseDto()
                .setEmail(doctor.getEmail())
                .setPassword(doctor.getPassword())
                .setFirstName(doctor.getFirstName())
                .setLastName(doctor.getLastName()));
    }

    private void initClinic() {
        log.trace("Checking if default clinic should be created on application startup");
        if (clinicService.isClinicExist()) {
            log.trace("The clinic found in DB; skipping creation...");
        } else {
            log.info("Default clinic not found, creating new one");
            var clinic = dataConfiguration.getClinicDefault();

            ClinicBaseDto clinicDto = new ClinicBaseDto().setName(clinic.getName())
                    .setEmail(clinic.getEmail())
                    .setPhone(clinic.getPhone())
                    .setFromHour(clinic.getFromHour())
                    .setToHour(clinic.getToHour());
            Resource logoResource = resourceLoader.getResource(clinic.getLogoUrl());

            try {
                String contentType =  Files.probeContentType(logoResource.getFile().toPath());
                clinicService.create(clinicDto, new LogoResourceDto(logoResource, contentType));

                log.info("Default clinic created successfully");
            } catch (Exception ex) {
                log.error("Failed to create the default clinic", ex);
            }
        }
    }
}

