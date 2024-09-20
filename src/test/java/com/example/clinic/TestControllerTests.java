package com.example.clinic;

import com.example.clinic.dto.AppointmentCreateDto;
import com.example.clinic.dto.TestCreateDto;
import com.example.clinic.dto.TestDto;
import com.example.clinic.model.Appointment;
import com.example.clinic.model.TestResult;
import com.example.clinic.model.TestType;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static com.example.clinic.BaseControllerTests.ADMIN_EMAIL;
import static com.example.clinic.BaseControllerTests.DOCTOR;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
public class TestControllerTests extends BaseControllerTests {

    @Value("classpath:reports/pdf-report.txt")
    private Resource pdfTextResource;

    private Appointment appointment;
    private LocalDateTime testDateTime;
    private LocalDateTime resultDateTime;

    @BeforeEach
    public void setUp() {
        patient = domainUtil.createPatient(dummyPatient);
        appointment = domainUtil.createAppointment(new AppointmentCreateDto(patient.getId()));

        testDateTime = LocalDateTime.of(2024, 3, 7, 12, 0);
        resultDateTime = LocalDateTime.of(2024, 3, 7, 13, 0);
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

    @Test
    @WithMockUser(username = PATIENT_EMAIL, roles = PATIENT)
    public void shouldGetPatientTests() throws Exception {

        // Arrange
        var test = createDummyTest();

        // Act
        var tests = doGetRequest("/api/tests", new TypeReference<List<TestDto>>() {
        });

        // Assert
        assertThat(tests).hasSize(1).first()
                .matches(t -> t.getId().equals(test.getId()))
                .matches(t -> t.getType() == TestType.TEST1)
                .matches(t -> t.getTestDateTime().equals(testDateTime))
                .matches(t -> t.getResult() == TestResult.POSITIVE)
                .matches(t -> t.getResultDateTime().equals(test.getResultDateTime()));
    }

    @Test
    @WithMockUser(username = PATIENT_EMAIL, roles = PATIENT)
    public void shouldGeneratePdf() throws Exception {

        // Arrange
        createDummyTest();

        // Act
        MvcResult mvcResult = this.mockMvc.perform(get("/api/tests/report")
                        .accept(MediaType.APPLICATION_PDF_VALUE))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andReturn();

        // Assert
        byte[] pdfBytes = mvcResult.getResponse().getContentAsByteArray();
        String text = pdfBytesAsString(pdfBytes);

        var expectedPdfText = new String(pdfTextResource.getContentAsByteArray(), UTF_8);
        assertThat(text).isEqualTo(expectedPdfText);
    }

    private String pdfBytesAsString(byte[] pdfBytes) throws Exception {
        InputStream inputStream = new ByteArrayInputStream(pdfBytes);
        PDDocument document = PDDocument.load(inputStream);

        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        return pdfTextStripper.getText(document);
    }

    private TestDto createDummyTest() {
        var testCreateDto = new TestCreateDto().setAppointmentId(appointment.getId());
        testCreateDto.setType(TestType.TEST1).setTestDateTime(testDateTime)
                .setResult(TestResult.POSITIVE).setResultDateTime(resultDateTime);
        return domainUtil.createTest(testCreateDto);
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
        doDelete("/api/tests/" + id);
    }

    private void deletePatient(Long id) throws Exception {
        doDelete("/api/patients/" + id);
    }
}