package com.example.clinic;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.model.Doctor;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.clinic.BaseTests.DOCTOR;
import static com.example.clinic.util.TestUtil.asJsonString;
import static com.example.clinic.util.TestUtil.fromJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "admin@achme.com", roles = DOCTOR)
public class DoctorTests extends BaseTests {

    @BeforeEach
    public void setUp() {
        domainUtil.createDoctor(dummyDoctor);
        domainUtil.createPatient(dummyPatient);
    }

    @Test
    public void shouldGetPaginatedAndSortedDoctors() throws Exception {
        // Act
        var doctors = fetchDoctors(0, 10);

        // Assert
        assertThat(doctors).hasSize(2)
                .map(Doctor::getFullName).containsExactlyInAnyOrder("Admin Admin", "Donny Doe");
    }

    @Test
    public void shouldGetSecondPageOfDoctors() throws Exception {

        // Act
        var doctors = fetchDoctors(1, 1);

        // Assert
        assertThat(doctors).hasSize(1)
                .map(Doctor::getFullName).containsExactlyInAnyOrder("Donny Doe");
    }

    @Test
    public void shouldCreateNewDoctor() throws Exception {

        // Arrange
        var dto = new PersonBaseDto("Sam", "Smith", "sam.brown@email.com", "12345678");

        // Act
        var doctor = createDoctor(dto);

        // Assert
        assertThat(doctor)
                .matches(d -> d.getFullName().equals("Sam Smith"))
                .matches(d -> d.getEmail().equals(dto.getEmail()))
                .matches(d -> d.getCreatedAt() != null);

        assertThat(domainUtil.getAllDoctors()).hasSize(3);
    }

    @Test
    public void shouldForbidCreatingDoctorWithSameEmail() throws Exception {

        // Arrange
        var dto = new PersonBaseDto("Alice", "Doe", "alice.doe@email.com", "12345678");

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/doctors")
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        assertThat(mvcResult.getResponse().getContentAsString())
                .contains("Doctor with email <alice.doe@email.com> already exists");
    }

    @Test
    public void shouldAllowCreatingDoctorWithSameEmailAsPatient() throws Exception {

        // Act
        var doctor = createDoctor(dummyPatient);

        // Assert
        assertThat(doctor)
                .matches(d -> d.getFullName().equals("Bob Bee"))
                .matches(d -> d.getEmail().equals(dummyPatient.getEmail()));

        assertThat(domainUtil.getAllDoctors()).hasSize(3);
    }

    private Doctor createDoctor(PersonBaseDto dto) throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/doctors")
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, Doctor.class);
    }

    private List<Doctor> fetchDoctors(int page, int size) throws Exception {
        var mvcResult = mockMvc.perform(get("/api/doctors")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, new TypeReference<>() {
        });
    }
}