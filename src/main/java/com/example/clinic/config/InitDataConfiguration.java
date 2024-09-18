package com.example.clinic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "init-data")
public class InitDataConfiguration {

    @NestedConfigurationProperty
    private AdminDoctorProperties adminDoctor;

    @NestedConfigurationProperty
    private ClinicDefaultProperties clinicDefault;

    @Getter
    @Setter
    public static class AdminDoctorProperties {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
    }

    @Getter
    @Setter
    public static class ClinicDefaultProperties {
        private String name;
        private String email;
        private String phone;
        private int fromHour;
        private int toHour;
        private String logoUrl;
        private boolean autoCreate;
    }
}