package com.example.clinic;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.model.Doctor;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.clinic.BaseTests.ADMIN_EMAIL;
import static com.example.clinic.BaseTests.DOCTOR;
import static com.example.clinic.util.TestUtil.asJsonString;
import static com.example.clinic.util.TestUtil.fromJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
public class DoctorTests extends BaseTests {

    private Doctor doctor;

    @BeforeEach
    public void setUp() {
        doctor = domainUtil.createDoctor(dummyDoctor);
        domainUtil.createPatient(dummyPatient);
    }

    @Test
    public void shouldGetPaginatedAndSortedDoctors() throws Exception {
        // Act
        var doctors = fetchDoctors(0, 10);

        // Assert
        assertThat(doctors).hasSize(2)
                .map(PersonDto::getFullName).containsExactlyInAnyOrder("Admin Admin", "Donny Doe");
    }

    @Test
    public void shouldGetSecondPageOfDoctors() throws Exception {

        // Act
        var doctors = fetchDoctors(1, 1);

        // Assert
        assertThat(doctors).hasSize(1)
                .map(PersonDto::getFullName).containsExactlyInAnyOrder("Donny Doe");
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
    public void shouldAllowCreatingDoctorWithSameEmailAsExistingPatient() throws Exception {

        // Act
        var doctor = createDoctor(dummyPatient);

        // Assert
        assertThat(doctor)
                .matches(d -> d.getFullName().equals("Bob Bee"))
                .matches(d -> d.getEmail().equals(dummyPatient.getEmail()));

        assertThat(domainUtil.getAllDoctors()).hasSize(3);
    }

    @Test
    public void shouldGetDoctorById() throws Exception {

        // Arrange & Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/doctors/" + doctor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        var doctor = fromJsonString(mvcResult, PersonDto.class);

        // Assert
        assertThat(doctor)
                .matches(d -> d.getFullName().equals("Donny Doe"))
                .matches(d -> d.getEmail().equals(dummyDoctor.getEmail()))
                .matches(d -> d.getPassword() == null);     // password is not exposed
    }

    @Test
    public void shouldUpdateExistingDoctor() throws Exception {

        // Arrange
        var dto = new PersonBaseDto("Shu", "Shoe", "shu.shoe@email.com", "1234567");

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/api/doctors/" + doctor.getId())
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var doctor = fromJsonString(mvcResult, PersonDto.class);
        assertThat(domainUtil.getAllDoctors()).hasSize(2);      // no new doctor created
        assertThat(doctor)
                .matches(d -> d.getFullName().equals("Shu Shoe"))
                .matches(d -> d.getEmail().equals(dto.getEmail()));
    }

    @Test
    public void shouldMarkDoctorAsDeleted() throws Exception {
        // Arrange
        assertThat(domainUtil.getAllDoctors()).hasSize(2);

        // Act
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/doctors/" + doctor.getId()))
                .andExpect(status().isNoContent())
                .andReturn();

        // Assert
        assertThat(domainUtil.getAllDoctors()).hasSize(1);

        var deletedDoctor = dbUtil.doSelect("select * from person where id = " + doctor.getId());
        assertThat(deletedDoctor).hasSize(1).matches(d -> d.get(0).get("DELETED").equals(true));
    }

    private PersonDto createDoctor(PersonBaseDto dto) throws Exception {
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/doctors")
                        .content(asJsonString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        return fromJsonString(mvcResult, PersonDto.class);
    }

    private List<PersonDto> fetchDoctors(int page, int size) throws Exception {
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