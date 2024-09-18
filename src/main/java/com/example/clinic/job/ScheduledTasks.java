package com.example.clinic.job;

import com.example.clinic.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final AppointmentService appointmentService;

    @Scheduled(cron = "${jobs.close-appointments-cron}")
    public void closeAppointments() {
        log.info("Closing appointments if there are any...");

        int processed = appointmentService.closeAppointments();
        log.info("Closed {} appointments", processed);
    }
}