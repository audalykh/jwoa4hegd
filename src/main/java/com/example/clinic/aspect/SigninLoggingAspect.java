package com.example.clinic.aspect;

import com.example.clinic.dto.JwtAuthenticationDto;
import com.example.clinic.dto.SignInDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.EntityType;
import com.example.clinic.model.Log;
import com.example.clinic.security.Role;
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
public class SigninLoggingAspect {

    private final LogService logService;

    @Pointcut(value = "bean(authenticationService) && execution(* signIn(..)) && args(signInDto, role)",
            argNames = "signInDto, role")
    public void signInPointcut(SignInDto signInDto, Role role) {
    }

    @AfterReturning(pointcut = "signInPointcut(signInDto, role)", returning = "result",
            argNames = "signInDto, role, result")
    public void afterSuccessfulSignIn(@SuppressWarnings("unused") SignInDto signInDto,
                                      Role role, JwtAuthenticationDto result) {
        var person = result.getPerson();
        log.trace("Authentication successful for user: {} with role: {}", person.getEmail(), role);

        var entityType = switch (role) {
            case DOCTOR -> EntityType.DOCTOR;
            case PATIENT -> EntityType.PATIENT;
        };
        var log = new Log().setActionType(ActionType.LOG_IN).setActorId(person.getId())
                .setEntityType(entityType).setEntityId(person.getId());
        logService.save(log);
    }
}