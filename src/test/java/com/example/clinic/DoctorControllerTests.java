package com.example.clinic;

import com.example.clinic.dto.PersonBaseDto;
import com.example.clinic.dto.PersonDto;
import com.example.clinic.model.ActionType;
import com.example.clinic.model.Doctor;
import com.example.clinic.model.EntityType;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithMockUser;

import static com.example.clinic.BaseControllerTests.ADMIN_EMAIL;
import static com.example.clinic.BaseControllerTests.DOCTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
public class DoctorControllerTests extends BaseControllerTests {

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

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.CREATE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.DOCTOR)
                .matches(l -> l.getEntityId().equals(doctor.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    public void shouldForbidCreatingDoctorWithSameEmail() throws Exception {

        // Arrange
        var dto = new PersonBaseDto("Alice", "Doe", "alice.doe@email.com", "12345678");

        // Act
        var mvcResult = doMvcRequest(dto, HttpMethod.POST, "/api/doctors", status().isBadRequest());

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
        var doctorDto = doGetRequest("/api/doctors/" + doctor.getId(), new TypeReference<PersonDto>() { });

        // Assert
        assertThat(doctorDto)
                .matches(d -> d.getFullName().equals("Donny Doe"))
                .matches(d -> d.getEmail().equals(dummyDoctor.getEmail()))
                .matches(d -> d.getPassword() == null);     // password is not exposed
    }

    @Test
    public void shouldUpdateExistingDoctor() throws Exception {

        // Arrange
        var dto = new PersonBaseDto("Shu", "Shoe", "shu.shoe@email.com", "1234567");

        // Act
        var doctorDto = doRequest(dto, HttpMethod.PUT, "/api/doctors/" + doctor.getId(), PersonDto.class);

        // Assert
        assertThat(domainUtil.getAllDoctors()).hasSize(2);      // no new doctor created
        assertThat(doctorDto)
                .matches(d -> d.getFullName().equals("Shu Shoe"))
                .matches(d -> d.getEmail().equals(dto.getEmail()));

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.UPDATE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.DOCTOR)
                .matches(l -> l.getEntityId().equals(doctor.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    @Test
    public void shouldMarkDoctorAsDeleted() throws Exception {
        // Arrange
        assertThat(domainUtil.getAllDoctors()).hasSize(2);

        // Act
        doDelete("/api/doctors/" + doctor.getId());

        // Assert
        assertThat(domainUtil.getAllDoctors()).hasSize(1);

        var deletedDoctor = dbUtil.doSelect("select * from person where id = " + doctor.getId());
        assertThat(deletedDoctor).hasSize(1).matches(d -> d.get(0).get("DELETED").equals(true));

        // Assert log entry has been created
        var log = domainUtil.getLogByType(ActionType.DELETE);
        assertThat(log)
                .matches(l -> l.getEntityType() == EntityType.DOCTOR)
                .matches(l -> l.getEntityId().equals(doctor.getId()))
                .matches(l -> l.getActorId().equals(adminDoctor.getId()));
    }

    private PersonDto createDoctor(PersonBaseDto dto) throws Exception {
        return doCreate(dto, HttpMethod.POST, "/api/doctors", PersonDto.class);
    }

    private List<PersonDto> fetchDoctors(int page, int size) throws Exception {
        return doGetRequest("/api/doctors",
                Map.of("page", String.valueOf(page), "size", String.valueOf(size)),
                new TypeReference<>() { });
    }
}