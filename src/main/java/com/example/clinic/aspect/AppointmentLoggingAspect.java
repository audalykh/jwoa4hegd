package com.example.clinic.aspect;

import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.EntityType;
import com.example.clinic.model.Log;
import com.example.clinic.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AppointmentLoggingAspect {

    private final LogService logService;

    @Pointcut(value = "bean(appointmentService) && execution(* create(..))")
    public void createPointcut() {
    }

    @Pointcut(value = "bean(appointmentService) && execution(* update(..))")
    public void updatePointcut() {
    }

    @Pointcut(value = "bean(appointmentService) && execution(* delete(..)) && args(id)", argNames = "id")
    public void deletePointcut(Long id) {
    }

    @AfterReturning(pointcut = "createPointcut()", returning = "result", argNames = "result")
    public void afterCreate(AppointmentDto result) {
        log.trace("Appointment created; id: {}, patient email: {}", result.getId(), result.getPatient().getEmail());
        saveLog(ActionType.CREATE, result.getId());
    }

    @AfterReturning(pointcut = "updatePointcut()", returning = "result", argNames = "result")
    public void afterUpdate(AppointmentDto result) {
        log.trace("Appointment updated; id: {}, patient email: {}", result.getId(), result.getPatient().getEmail());
        saveLog(ActionType.UPDATE, result.getId());
    }

    @AfterReturning(pointcut = "deletePointcut(id)", argNames = "id")
    public void afterDelete(Long id) {
        log.trace("Appointment deleted; id: {}", id);
        saveLog(ActionType.DELETE, id);
    }

    private void saveLog(ActionType actionType, Long id) {
        var log = new Log().setActionType(actionType).setEntityType(EntityType.APPOINTMENT).setEntityId(id);
        logService.save(log);
    }
}