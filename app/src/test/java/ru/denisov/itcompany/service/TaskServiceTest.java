package ru.denisov.itcompany.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.denisov.core.entities.Project;
import ru.denisov.core.entities.Task;
import ru.denisov.itcompany.dto.TaskCreateRequest;
import ru.denisov.itcompany.dto.TaskResponse;
import ru.denisov.itcompany.dto.TaskUpdateRequest;
import ru.denisov.itcompany.repository.ProjectRepository;
import ru.denisov.itcompany.repository.TaskRepository;
import ru.denisov.itcompany.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository; // Замокан, так как инжектируется в сервис, но в текущих методах не используется
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    private UUID taskId;
    private UUID projectId;
    private Instant now;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        now = Instant.now();

        project = Project.builder().id(projectId).build();
        task = Task.builder()
                .id(taskId)
                .name("Original Task")
                .project(project)
                .createdAt(now)
                .endedAt(now.plusSeconds(3600))
                .build();
    }

    // ==================== CREATE ====================

    @Test
    void create_ShouldSaveAndReturnResponse() {
        var request = new TaskCreateRequest("New Task", projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.create(request);

        assertThat(response.id()).isEqualTo(taskId);
        assertThat(response.name()).isEqualTo(task.getName());
        assertThat(response.projectId()).isEqualTo(projectId);

        verify(projectRepository).findById(projectId);
        verify(taskRepository).save(argThat(t -> t.getName().equals("New Task")));
    }

    @Test
    void create_WhenProjectNotFound_ShouldThrowException() {
        var request = new TaskCreateRequest("New Task", projectId);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");

        verify(taskRepository, never()).save(any());
    }

    // ==================== GET BY ID ====================

    @Test
    void getById_WhenExists_ShouldReturnResponse() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getById(taskId);

        assertThat(response.id()).isEqualTo(taskId);
        assertThat(response.name()).isEqualTo("Original Task");
        assertThat(response.projectId()).isEqualTo(projectId);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getById(taskId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found with id: " + taskId);
    }

    // ==================== GET ALL / BY PROJECT ====================

    @Test
    void getAll_ShouldReturnListOfResponses() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponse> responses = taskService.getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(taskId);
    }

    @Test
    void getAllByProjectId_ShouldReturnFilteredResponses() {
        when(taskRepository.getByProjectId(projectId)).thenReturn(List.of(task));

        List<TaskResponse> responses = taskService.getAllByProjectId(projectId);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().projectId()).isEqualTo(projectId);
        verify(taskRepository).getByProjectId(projectId);
    }

    // ==================== UPDATE (Ключевой блок для покрытия) ====================

    @Test
    void updateById_WithFullData_ShouldUpdateAllFields() {
        UUID newProjectId = UUID.randomUUID();
        Project newProject = Project.builder().id(newProjectId).build();
        Instant newStart = now.minusSeconds(100);
        Instant newEnd = now.plusSeconds(7200);

        var request = new TaskUpdateRequest("Updated Name", newStart, newEnd, newProjectId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.of(newProject));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.updateById(taskId, request);

        assertThat(response.name()).isEqualTo("Updated Name");
        verify(projectRepository).findById(newProjectId);
        verify(taskRepository).save(any());
    }

    @Test
    void updateById_WithPartialNameOnly_ShouldNotTouchOtherFields() {
        var request = new TaskUpdateRequest("Only Name", null, null, null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateById(taskId, request);

        assertThat(task.getName()).isEqualTo("Only Name");
        // Проверяем, что проект НЕ запрашивался, так как projectId = null
        verify(projectRepository, never()).findById(any());
        verify(taskRepository).save(any());
    }

    @Test
    void updateById_WithPartialProjectOnly_ShouldValidateProject() {
        UUID newProjectId = UUID.randomUUID();
        Project newProject = Project.builder().id(newProjectId).build();
        var request = new TaskUpdateRequest(null, null, null, newProjectId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.of(newProject));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateById(taskId, request);

        verify(projectRepository).findById(newProjectId);
        verify(taskRepository).save(any());
    }

    @Test
    void updateById_WhenTaskNotFound_ShouldThrowException() {
        var request = new TaskUpdateRequest("Name", null, null, null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateById(taskId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");

        verify(taskRepository, never()).save(any());
        verify(projectRepository, never()).findById(any());
    }

    @Test
    void updateById_WhenNewProjectNotFound_ShouldThrowException() {
        UUID newProjectId = UUID.randomUUID();
        var request = new TaskUpdateRequest(null, null, null, newProjectId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateById(taskId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");

        verify(taskRepository, never()).save(any());
    }

    // ==================== DELETE ====================

    @Test
    void deleteById_ShouldCallRepositoryAndReturnTrue() {
        doNothing().when(taskRepository).deleteById(taskId);

        Boolean result = taskService.deleteById(taskId);

        assertThat(result).isTrue();
        verify(taskRepository).deleteById(taskId);
    }
}
