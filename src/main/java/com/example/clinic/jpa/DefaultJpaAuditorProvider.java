package com.example.clinic.jpa;

import com.example.clinic.security.CustomUsernamePasswordAuthenticationToken;
import java.util.Optional;
import org.springframework.security.core.Authentication;

public class DefaultJpaAuditorProvider extends BaseJpaAuditorProvider {

    @Override
    protected Optional<Long> getAuditorId(Authentication authentication) {
        if (authentication instanceof CustomUsernamePasswordAuthenticationToken token) {
            return Optional.of(token.getActorId());
        }
        return Optional.empty();
    }
}