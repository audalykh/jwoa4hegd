package com.example.clinic.service;

import com.example.clinic.dto.PersonBaseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitializationService {

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    @Value("${admin-doctor.email}")
    private String adminEmail;
    @Value("${admin-doctor.email}")
    private String adminPassword;

    private final DoctorService doctorService;

    @EventListener(ContextRefreshedEvent.class)
    public void onStartupEvent() {
        log.trace("Checking if am admin doctor should be created on application startup");

        Assert.isTrue(EMAIL_VALIDATOR.isValid(adminEmail),
                "Invalid email address provided for admin doctor: " + adminEmail);

        var adminDoctor = doctorService.findByEmail(adminEmail);
        if (adminDoctor == null) {
            log.info("Admin doctor not found, creating new one");
            String name = adminEmail.split("@")[0];

            doctorService.create(new PersonBaseDto()
                    .setEmail(adminEmail)
                    .setPassword(adminPassword)
                    .setFirstName(name)
                    .setLastName(name));
        } else {
            log.trace("Admin doctor found; skipping creation...");
        }
    }
}

