package com.example.clinic.aspect;

import com.example.clinic.dto.PersonDto;
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
public class DoctorLoggingAspect {

    private final LogService logService;

    @Pointcut(value = "bean(doctorService) && execution(* create(..))")
    public void createPointcut() {
    }

    @Pointcut(value = "bean(doctorService) && execution(* update(..))")
    public void updatePointcut() {
    }

    @Pointcut(value = "bean(doctorService) && execution(* delete(..)) && args(id)", argNames = "id")
    public void deletePointcut(Long id) {
    }

    @AfterReturning(pointcut = "createPointcut()", returning = "result", argNames = "result")
    public void afterCreate(PersonDto result) {
        log.trace("Doctor created; id: {}, email: {}", result.getId(), result.getEmail());
        saveLog(ActionType.CREATE, result.getId());
    }

    @AfterReturning(pointcut = "updatePointcut()", returning = "result", argNames = "result")
    public void afterUpdate(PersonDto result) {
        log.trace("Doctor updated; id: {}, email: {}", result.getId(), result.getEmail());
        saveLog(ActionType.UPDATE, result.getId());
    }

    @AfterReturning(pointcut = "deletePointcut(id)", argNames = "id")
    public void afterDelete(Long id) {
        log.trace("Doctor deleted; id: {}", id);
        saveLog(ActionType.DELETE, id);
    }

    private void saveLog(ActionType actionType, Long id) {
        var log = new Log().setActionType(actionType).setEntityType(EntityType.DOCTOR).setEntityId(id);
        logService.save(log);
    }
}