package com.example.clinic.aspect;

import com.example.clinic.dto.PersonDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.Appointment;
import com.example.clinic.model.EntityType;
import com.example.clinic.model.Log;
import com.example.clinic.service.LogService;
import com.example.clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * This class is an aspect that provides logging functionality for the PatientService class.
 * It logs the creation, update, and deletion of patients, as well as their associated appointments.
 * The logs are saved using the LogService class.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PatientLoggingAspect {

    private final LogService logService;
    private final PatientService patientService;

    @Pointcut(value = "bean(patientService) && execution(* create(..))")
    public void createPointcut() {
    }

    @Pointcut(value = "bean(patientService) && execution(* update(..))")
    public void updatePointcut() {
    }

    @Pointcut(value = "bean(patientService) && execution(* delete(..)) && args(id)", argNames = "id")
    public void deletePointcut(Long id) {
    }

    @AfterReturning(pointcut = "createPointcut()", returning = "result", argNames = "result")
    public void afterCreate(PersonDto result) {
        log.trace("Patient created; id: {}, email: {}", result.getId(), result.getEmail());
        saveLog(ActionType.CREATE, result.getId());
    }

    @AfterReturning(pointcut = "updatePointcut()", returning = "result", argNames = "result")
    public void afterUpdate(PersonDto result) {
        log.trace("Patient updated; id: {}, email: {}", result.getId(), result.getEmail());
        saveLog(ActionType.UPDATE, result.getId());
    }

    @Around(value = "deletePointcut(id)", argNames = "joinPoint, id")
    public Object aroundDelete(ProceedingJoinPoint joinPoint, Long id) throws Throwable {
        var patient = patientService.findByIdOrThrow(id);
        var appointmentIds = patient.getAppointments().stream().map(Appointment::getId).toList();

        try {
            return joinPoint.proceed();
        } finally {
            log.trace("Patient deleted; id: {}", id);
            saveLog(ActionType.DELETE, id);

            // Appointments are going to be deleted in a cascade manner, so need to log them as well
            var logs = appointmentIds.stream().map(appointmentId -> new Log().setActionType(ActionType.DELETE)
                    .setEntityType(EntityType.APPOINTMENT).setEntityId(appointmentId)).toList();
            logService.saveAll(logs);
        }
    }
    private void saveLog(ActionType actionType, Long id) {
        var log = new Log().setActionType(actionType).setEntityType(EntityType.PATIENT).setEntityId(id);
        logService.save(log);
    }
}