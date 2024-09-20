package com.example.clinic;

import com.example.clinic.config.InitDataConfiguration;
import com.example.clinic.dto.JwtAuthenticationDto;
import com.example.clinic.dto.SignInDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.EntityType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.clinic.util.TestUtil.asJsonString;
import static com.example.clinic.util.TestUtil.fromJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthControllerTests extends BaseControllerTests {

    @Autowired
    private InitDataConfiguration initDataConfiguration;

    @Test
    void shouldSignInAsExistingDoctor() throws Exception {
        // Arrange & Act
        var jwtAuthenticationDto = loginAsDoctor();

        var admin = initDataConfiguration.getAdminDoctor();

        assertThat(jwtAuthenticationDto.getJwt()).isNotNull();
        assertThat(jwtAuthenticationDto.getPerson())
                .matches(person -> person.getEmail().equals(admin.getEmail()))
                .matches(person -> person.getLastLoginAt() != null)
                .matches(person -> person.getCreatedAt() != null);
    }

    @Test
    public void shouldCreateLogEntryOnSuccessfulDoctorSignIn() throws Exception {
        // Arrange & Act
        var jwtAuthenticationDto = loginAsDoctor();

        // Assert
        var person = jwtAuthenticationDto.getPerson();

        var logs = domainUtil.getAllLogs();
        assertThat(logs).hasSize(1);

        assertThat(logs.get(0))
                .matches(log -> log.getActionType() == ActionType.LOG_IN)
                .matches(log -> log.getEntityType() == EntityType.DOCTOR)
                .matches(log -> log.getEntityId().equals(person.getId()))
                .matches(log -> log.getActorId().equals(person.getId()));
    }

    @Test
    public void shouldFailOnSignInAttemptWithInvalidDoctorCreds() throws Exception {
        // Arrange
        var signInDto = new SignInDto().setEmail("foo").setPassword("bar");

        // Act & Assert
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-in/doctor")
                        .content(asJsonString(signInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andReturn();

        assertThat(mvcResult.getResponse().getContentAsString()).contains("Bad credentials");
    }

    @Test
    public void shouldSignInAsPatient() throws Exception {
        // Arrange & Act
        var jwtAuthenticationDto = loginAsPatient();

        assertThat(jwtAuthenticationDto.getJwt()).isNotNull();
        assertThat(jwtAuthenticationDto.getPerson().getEmail()).isEqualTo(dummyPatient.getEmail());
    }

    @Test
    public void shouldCreateLogEntryOnSuccessfulPatientSignIn() throws Exception {
        // Arrange & Act
        var jwtAuthenticationDto = loginAsPatient();

        // Assert
        var person = jwtAuthenticationDto.getPerson();
        var logs = domainUtil.getAllLogs();
        assertThat(logs).hasSize(1);

        assertThat(logs.get(0))
                .matches(log -> log.getActionType() == ActionType.LOG_IN)
                .matches(log -> log.getEntityType() == EntityType.PATIENT)
                .matches(log -> log.getEntityId().equals(person.getId()))
                .matches(log -> log.getActorId().equals(person.getId()));
    }

    private JwtAuthenticationDto loginAsDoctor() throws Exception {
        // Arrange
        var admin = initDataConfiguration.getAdminDoctor();
        var signInDto = new SignInDto().setEmail(admin.getEmail()).setPassword(admin.getPassword());

        // Act
        var mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-in/doctor")
                        .content(asJsonString(signInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, JwtAuthenticationDto.class);
    }

    private JwtAuthenticationDto loginAsPatient() throws Exception {
        // Arrange
        var patient = domainUtil.createPatient(dummyPatient);

        var signInDto = new SignInDto().setEmail(patient.getEmail()).setPassword(dummyPatient.getPassword());

        // Act
        var mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/sign-in/patient")
                        .content(asJsonString(signInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, JwtAuthenticationDto.class);
    }
}