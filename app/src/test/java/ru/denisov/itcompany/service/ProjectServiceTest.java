package ru.denisov.itcompany.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.denisov.core.entities.Project;
import ru.denisov.itcompany.dto.ProjectCreateRequest;
import ru.denisov.itcompany.dto.ProjectResponse;
import ru.denisov.itcompany.dto.ProjectUpdateRequest;
import ru.denisov.itcompany.repository.ProjectRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private UUID projectId;
    private Instant now;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        now = Instant.now();
        project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .createdAt(now)
                .endedAt(now.plusSeconds(30))
                .build();
    }

    @Test
    void create_ShouldSaveAndReturnResponse() {
        ProjectCreateRequest request = new ProjectCreateRequest("New Project");

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.create(request);

        assertThat(response.id()).isEqualTo(projectId);
        assertThat(response.name()).isEqualTo(project.getName());
        assertThat(response.startedAt()).isEqualTo(project.getCreatedAt());
        assertThat(response.endedAt()).isEqualTo(project.getEndedAt());

        verify(projectRepository).save(argThat(p -> p.getName().equals(request.name())));
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void getById_WhenExists_ShouldReturnResponse() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.getById(projectId);

        assertThat(response.id()).isEqualTo(projectId);
        assertThat(response.name()).isEqualTo("Test Project");

        verify(projectRepository).findById(projectId);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowEntityNotFoundException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(projectId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found with id: " + projectId);

        verify(projectRepository).findById(projectId);
    }

    @Test
    void getAll_ShouldReturnListOfResponses() {
        Project anotherProject = Project.builder()
                .id(UUID.randomUUID())
                .name("Another")
                .createdAt(now.minusSeconds(1))
                .endedAt(now.plusSeconds(10))
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(project, anotherProject));

        List<ProjectResponse> responses = projectService.getAll();

        assertThat(responses).hasSize(2);
        assertThat(responses).extracting(ProjectResponse::name)
                .containsExactly("Test Project", "Another");

        verify(projectRepository).findAll();
    }

    @Test
    void updateById_WhenExists_ShouldUpdateAndReturn() {
        UUID id = projectId;
        ProjectUpdateRequest request = new ProjectUpdateRequest(
                "Updated Name",
                now.minusSeconds(5),
                now.plusSeconds(60)
        );

        when(projectRepository.findById(id)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse response = projectService.updateById(id, request);

        assertThat(response.name()).isEqualTo("Updated Name");
        assertThat(project.getName()).isEqualTo("Updated Name");
        assertThat(project.getCreatedAt()).isEqualTo(now.minusSeconds(5));
        assertThat(project.getEndedAt()).isEqualTo(now.plusSeconds(60));

        verify(projectRepository).findById(id);
        verify(projectRepository).save(project);
    }

    @Test
    void updateById_WhenNotExists_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.updateById(id, mock(ProjectUpdateRequest.class)))
                .isInstanceOf(EntityNotFoundException.class);

        verify(projectRepository).findById(id);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateById_WithPartialData_ShouldUpdateOnlyNonNullFields() {
        ProjectUpdateRequest request = new ProjectUpdateRequest("Only Name", null, null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        projectService.updateById(projectId, request);

        assertThat(project.getName()).isEqualTo("Only Name");
        assertThat(project.getCreatedAt()).isEqualTo(now);
        assertThat(project.getEndedAt()).isEqualTo(now.plusSeconds(30));
    }

    @Test
    void deleteById_ShouldCallRepositoryAndReturnTrue() {
        doNothing().when(projectRepository).deleteById(projectId);

        Boolean result = projectService.deleteById(projectId);

        assertThat(result).isTrue();
        verify(projectRepository).deleteById(projectId);
    }
}
