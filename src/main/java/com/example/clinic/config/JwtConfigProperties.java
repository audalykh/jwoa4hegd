package com.example.clinic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JwtConfigProperties is a configuration class that provides properties related to JWT (JSON Web Tokens).
 * It is used to specify the secret key and expiration time for JWT generation and validation.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtConfigProperties {

    private String secretKey;
    private long expirationTimeMinutes;
}