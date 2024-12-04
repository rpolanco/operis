package com.operis.project.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.operis.project.adapter.in.rest.payload.CreateProjectPayload;
import com.operis.project.core.domain.CreateProjectCommand;
import com.operis.project.core.domain.Project;
import com.operis.project.core.port.in.CreateProjectUseCase;
import com.operis.project.infrastructure.jwt.JWTConnectedUserResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateProjectUseCase createProjectUseCase;

    @MockBean
    private JWTConnectedUserResolver jwtTokenService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldCreateProject() throws Exception {
        // Given
        CreateProjectPayload createProjectPayload = new CreateProjectPayload(
                "Create Operis Web App", "Create an IT product allowing to manage projects."
        );

        when(jwtTokenService.extractConnectedUserEmail(anyString()))
                .thenReturn("imad.test@gmail.com");

        when(createProjectUseCase.createProject(any(CreateProjectCommand.class)))
                .thenReturn(new Project(
                        createProjectPayload.name(),
                        createProjectPayload.description(),
                        "imad.test@gmail.com"
                ));

        // When
        mockMvc.perform(post("/api/projects")
                        .header("Authorization", "Bearer 1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProjectPayload)))
                // Then
                .andExpect(status().isCreated());
    }
}
