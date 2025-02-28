package com.example.clinic;

import com.example.clinic.dto.ClinicDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.example.clinic.util.TestUtil.fromJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClinicControllerTests extends BaseControllerTests {

    @Value("classpath:images/new-logo.png")
    private Resource logoResource;

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
    public void shouldGetExistingClinicByDoctor() throws Exception {
        // Act
        ClinicDto clinic = doGetRequest("/api/clinic", new TypeReference<>() { });

        // Assert
        assertThat(clinic.getName()).isEqualTo("Achme Clinic");
        assertThat(clinic.getLogo().getName()).isEqualTo("clinic-logo.png");
    }

    @Test
    @WithMockUser(username = "bob", roles = PATIENT)
    public void shouldGetExistingClinicByPatient() throws Exception {
        // Act
        ClinicDto dto = doGetRequest("/api/clinic", new TypeReference<>() { });

        // Assert
        assertThat(dto).isNotNull();
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
    public void shouldUpdateClinic() throws Exception {

        // Arrange
        MockMultipartFile file = buildMockMultipartFile();
        MultiValueMap<String, String> multiValueMap = buildClinicRequestMap();

        // Act
        var mvcResult = doMultiPartRequest(HttpMethod.PUT, file, multiValueMap, status().isOk());

        // Assert
        assertClientFields(mvcResult);
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
    public void shouldCreateNewClinic() throws Exception {

        // Arrange: delete the only clinic
        doDelete("/api/clinic");
        assertClinicsCount(0);

        MockMultipartFile file = buildMockMultipartFile();
        MultiValueMap<String, String> multiValueMap = buildClinicRequestMap();

        // Act
        var mvcResult = doMultiPartRequest(HttpMethod.POST, file, multiValueMap, status().isCreated());

        // Assert
        assertClientFields(mvcResult);
        assertClinicsCount(1);
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = DOCTOR)
    public void shouldFailToCreateAnotherClinic() throws Exception {

        // Arrange
        MockMultipartFile file = buildMockMultipartFile();
        MultiValueMap<String, String> multiValueMap = buildClinicRequestMap();

        // Act
        var mvcResult = doMultiPartRequest(HttpMethod.POST, file, multiValueMap, status().isBadRequest());

        // Assert
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Clinic already exist");
    }

    private MvcResult doMultiPartRequest(HttpMethod method, MockMultipartFile file,
            MultiValueMap<String, String> multiValueMap, ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.multipart(method, "/api/clinic")
                        .file(file)
                        .params(multiValueMap))
                .andExpect(resultMatcher)
                .andReturn();
    }

    private void assertClinicsCount(int size) {
        var dbClinics = dbUtil.doSelect("select * from clinic");
        assertThat(dbClinics).hasSize(size);
    }

    private MultiValueMap<String, String> buildClinicRequestMap() {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("name", "New Clinic");
        multiValueMap.add("email", "new.mail@email.com");
        multiValueMap.add("phone", "987654321");
        multiValueMap.add("fromHour", "11");
        multiValueMap.add("toHour", "16");
        return multiValueMap;
    }

    private void assertClientFields(MvcResult mvcResult) {
        assertThat(fromJsonString(mvcResult, ClinicDto.class))
                .matches(clinic -> clinic.getName().equals("New Clinic"))
                .matches(clinic -> clinic.getEmail().equals("new.mail@email.com"))
                .matches(clinic -> clinic.getPhone().equals("987654321"))
                .matches(clinic -> clinic.getFromHour().equals(11))
                .matches(clinic -> clinic.getToHour().equals(16))
                .matches(clinic -> clinic.getLogo().getName().equals("new-logo.png"))
                .matches(clinic -> clinic.getLogo().getType().equals("image/png"));
    }

    private MockMultipartFile buildMockMultipartFile() throws Exception {
        try (var in = logoResource.getInputStream()) {
            return new MockMultipartFile("logo", "new-logo.png", "image/png", in);
        }
    }
}
