package com.openclassrooms.starterjwt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TeacherControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        String loginPayload = "{\"email\":\"test.user@example.com\",\"password\":\"dummypassword\"}";
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andReturn()
                .getResponse()
                .getContentAsString();

        token = objectMapper.readTree(response).get("token").asText();
    }

    private String getAuthorizationHeader() {
        return "Bearer " + token;
    }

    @Test
    void shouldReturnAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher")
        .header("Authorization", getAuthorizationHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldReturnTeacherById() throws Exception {
        mockMvc.perform(get("/api/teacher/1")
        .header("Authorization", getAuthorizationHeader()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.lastName").value("DELAHAYE"));
    }

    @Test
    void shouldReturnNotFoundWhenTeacherDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/teacher/999")
        .header("Authorization", getAuthorizationHeader()))
        .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/api/teacher/abc")
        .header("Authorization", getAuthorizationHeader()))
        .andExpect(status().isBadRequest());
    }
}
