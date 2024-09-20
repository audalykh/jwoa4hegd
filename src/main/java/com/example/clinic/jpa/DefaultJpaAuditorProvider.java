package com.example.clinic.jpa;

import com.example.clinic.security.CustomUsernamePasswordAuthenticationToken;
import java.util.Optional;
import org.springframework.security.core.Authentication;

/**
 * Default implementation of the JpaAuditorProvider interface.
 * It extends the BaseJpaAuditorProvider class and provides the logic to retrieve the auditor
 * ID from the authentication object.
 */
public class DefaultJpaAuditorProvider extends BaseJpaAuditorProvider {

    /**
     * Retrieves the auditor ID from the authentication object; it used to populate domain entity fields
     * annotated with @CreatedBy or @LastModifiedBy during JPA auditing.
     */
    @Override
    protected Optional<Long> getAuditorId(Authentication authentication) {
        if (authentication instanceof CustomUsernamePasswordAuthenticationToken token) {
            return Optional.of(token.getActorId());
        }
        return Optional.empty();
    }
}