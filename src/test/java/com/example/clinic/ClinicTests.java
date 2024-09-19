package com.example.clinic;

import com.example.clinic.dto.ClinicDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.example.clinic.util.TestUtil.fromJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClinicTests extends BaseTests {

    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    @WithMockUser(username = "adminDoctor", roles = DOCTOR)
    public void shouldGetExistingClinicByDoctor() throws Exception {
        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/clinic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        var clinic = fromJsonString(mvcResult, ClinicDto.class);
        assertThat(clinic.getName()).isEqualTo("Achme Clinic");
        assertThat(clinic.getLogo().getName()).isEqualTo("clinic-logo.png");
    }

    @Test
    @WithMockUser(username = "bob", roles = PATIENT)
    public void shouldGetExistingClinicByPatient() throws Exception {
        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/clinic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        assertThat(fromJsonString(mvcResult, ClinicDto.class)).isNotNull();
    }

    @Test
    @WithMockUser(username = "adminDoctor", roles = DOCTOR)
    public void shouldUpdateClinic() throws Exception {

        // Arrange
        MockMultipartFile file = buildMockMultipartFile();
        MultiValueMap<String, String> multiValueMap = buildClinicRequestMap();

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PUT, "/api/clinic")
                        .file(file)
                        .params(multiValueMap))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        assertClientFields(mvcResult);
    }

    @Test
    @WithMockUser(username = "adminDoctor", roles = DOCTOR)
    public void shouldCreateNewClinic() throws Exception {

        // Arrange: delete the only clinic
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/clinic")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        assertClinicsCount(0);

        MockMultipartFile file = buildMockMultipartFile();
        MultiValueMap<String, String> multiValueMap = buildClinicRequestMap();

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/clinic")
                        .file(file)
                        .params(multiValueMap))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        assertClientFields(mvcResult);
        assertClinicsCount(1);
    }

    @Test
    @WithMockUser(username = "adminDoctor", roles = DOCTOR)
    public void shouldFailToCreateAnotherClinic() throws Exception {

        // Arrange
        MockMultipartFile file = buildMockMultipartFile();
        MultiValueMap<String, String> multiValueMap = buildClinicRequestMap();

        // Act
        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.POST, "/api/clinic")
                        .file(file)
                        .params(multiValueMap))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        assertThat(mvcResult.getResponse().getContentAsString()).contains("Clinic already exist");
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
        var logoResource = resourceLoader.getResource("classpath:images/new-logo.png");
        try (var in = logoResource.getInputStream()) {
            return new MockMultipartFile("logo", "new-logo.png", "image/png", in);
        }
    }
}
