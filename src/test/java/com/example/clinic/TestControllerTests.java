package com.example.clinic;

import com.example.clinic.dto.AppointmentCreateDto;
import com.example.clinic.dto.AppointmentDto;
import com.example.clinic.dto.TestCreateDto;
import com.example.clinic.dto.TestDto;
import com.example.clinic.model.TestResult;
import com.example.clinic.model.TestType;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
public class TestControllerTests extends BaseControllerTests {

    private AppointmentDto appointment;
    private LocalDateTime testDateTime;

    @BeforeEach
    public void setUp() throws Exception {
        patient = domainUtil.createPatient(dummyPatient);
        appointment = createAppointment(new AppointmentCreateDto(patient.getId()));
        testDateTime = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES);
    }

    @Test
    public void shouldCreateTestForAppointment() throws Exception {

        // Act
        var test = createTest();

        // Assert
        assertThat(test)
                .matches(t -> t.getType() == TestType.TEST1)
                .matches(t -> t.getTestDateTime().equals(testDateTime))
                .matches(t -> t.getResult() == TestResult.UNKNOWN)
                .matches(t -> t.getResultDateTime() == null);
    }

    @Test
    public void shouldUpdateTestForAppointment() throws Exception {

        // Arrange
        var test = createTest();

        var testDateTime = LocalDateTime.now().plusHours(1).truncatedTo(ChronoUnit.MINUTES);
        var resultDateTime = LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES);
        test.setType(TestType.TEST2).setTestDateTime(testDateTime)
                .setResult(TestResult.POSITIVE).setResultDateTime(resultDateTime);

        // Act
        test = updateTest(test);

        // Assert
        assertThat(test)
                .matches(t -> t.getType() == TestType.TEST2)
                .matches(t -> t.getTestDateTime().equals(testDateTime))
                .matches(t -> t.getResult() == TestResult.POSITIVE)
                .matches(t -> t.getResultDateTime().equals(resultDateTime));
    }

    @Test
    public void shouldMarkTestAsDeleted() throws Exception {

        // Arrange
        var test = createTest();
        assertThat(domainUtil.getPatientTests(patient)).hasSize(1);

        // Act
        deleteTest(test.getId());

        // Assert
        assertThat(domainUtil.getPatientTests(patient)).isEmpty();

        var deletedTests = dbUtil.doSelect("select * from test where id = " + test.getId());
        assertThat(deletedTests).hasSize(1).matches(t -> t.get(0).get("DELETED").equals(true));
    }

    @Test
    public void shouldDeleteTestsAlongWithOwningPatient() throws Exception {

        // Arrange
        createTest();
        assertThat(domainUtil.getAllTests()).hasSize(1);

        // Act
        deletePatient(patient.getId());

        // Assert
        assertThat(domainUtil.getAllTests()).isEmpty();
        assertThat(domainUtil.getAllPatients()).isEmpty();
    }

    private TestDto createTest() throws Exception {
        var createDto = new TestCreateDto().setAppointmentId(appointment.getId())
                .setType(TestType.TEST1).setTestDateTime(testDateTime);
        return createTest(createDto);
    }

    private TestDto createTest(TestDto dto) throws Exception {
        return doRequest(dto, HttpMethod.POST, "/api/tests", TestDto.class, status().isCreated());
    }

    private TestDto updateTest(TestDto dto) throws Exception {
        return doRequest(dto, HttpMethod.PUT, "/api/tests/" + dto.getId(), TestDto.class);
    }

    private void deleteTest(Long id) throws Exception {
        doRequest(Map.of(), HttpMethod.DELETE, "/api/tests/" + id, Void.class, status().isNoContent());
    }

    private void deletePatient(Long id) throws Exception {
        doRequest(Map.of(), HttpMethod.DELETE, "/api/patients/" + id, Void.class, status().isNoContent());
    }

    private AppointmentDto createAppointment(AppointmentCreateDto dto) throws Exception {
        return doRequest(dto, HttpMethod.POST, "/api/appointments", AppointmentDto.class, status().isCreated());
    }
}