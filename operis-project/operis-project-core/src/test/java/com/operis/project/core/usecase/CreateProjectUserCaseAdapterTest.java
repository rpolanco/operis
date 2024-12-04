package com.operis.project.core.usecase;

import com.operis.project.core.domain.CreateProjectCommand;
import com.operis.project.core.domain.Project;
import com.operis.project.core.port.out.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreateProjectUserCaseAdapterTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private CreateProjectUserCaseAdapter createProjectUseCase;

    @ParameterizedTest
    @CsvSource({
            "'', ronald.test@gmail.com, Project name cannot be null or empty",
            "'  ', ronald.test@gmail.com, Project name cannot be null or empty",
            ", ronald.test@gmail.com, Project name cannot be null or empty",
            "Operis, '', Project owner cannot be null or empty",
            "operis, '  ', Project owner cannot be null or empty",
            "Operis, , Project owner cannot be null or empty"
    })
    void shouldThrowIllegalArgumentExceptionGivenInvalidProjectFields(String name, String owner, String expectedMessage) {
        // GIVEN
        CreateProjectCommand command = new CreateProjectCommand(
                name, "description", owner);

        // WHEN
        var actualException = assertThrows(IllegalArgumentException.class,
                () -> createProjectUseCase.createProject(command));

        // THEN
        assertEquals(expectedMessage, actualException.getMessage());
    }

    @Test
    void shouldCreateProject() {
        // GIVEN
        CreateProjectCommand command = new CreateProjectCommand(
                "Operis", "Project description", "ronald.test@gmail.com");

        // WHEN
        createProjectUseCase.createProject(command);

        // THEN
        ArgumentCaptor<Project> projectArgumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.verify(projectRepository).create(projectArgumentCaptor.capture());

        Project actualProject = projectArgumentCaptor.getValue();
        assertNotNull(actualProject.getId());
        assertEquals("Operis", actualProject.getName());
        assertEquals("ronald.test@gmail.com", actualProject.getOwner());
        assertEquals(List.of("ronald.test@gmail.com"), actualProject.getMembers());
    }
}