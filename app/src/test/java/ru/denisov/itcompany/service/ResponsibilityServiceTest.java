package ru.denisov.itcompany.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.denisov.core.entities.Responsibility;
import ru.denisov.core.entities.Task;
import ru.denisov.core.entities.User;
import ru.denisov.itcompany.dto.ResponsibilityCreateRequest;
import ru.denisov.itcompany.dto.ResponsibilityResponse;
import ru.denisov.itcompany.dto.ResponsibilityUpdateRequest;
import ru.denisov.itcompany.repository.ResponsibilityRepository;
import ru.denisov.itcompany.repository.TaskRepository;
import ru.denisov.itcompany.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponsibilityServiceTest {
    @Mock
    private ResponsibilityRepository responsibilityRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResponsibilityService responsibilityService;

    private UUID responsibilityId;
    private UUID taskId;
    private UUID userId;
    private Task task;
    private User user;
    private Responsibility responsibility;

    @BeforeEach
    void setUp() {
        responsibilityId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();

        task = Task.builder().id(taskId).build();
        user = User.builder().id(userId).build();

        responsibility = Responsibility.builder()
                .id(responsibilityId)
                .task(task)
                .user(user)
                .build();
    }

    @Test
    void create_ShouldSaveAndReturnResponse() {
        var request = new ResponsibilityCreateRequest(taskId, userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(responsibilityRepository.save(any(Responsibility.class))).thenReturn(responsibility);

        ResponsibilityResponse response = responsibilityService.create(request);

        assertThat(response.id()).isEqualTo(responsibilityId);
        assertThat(response.taskId()).isEqualTo(taskId);
        assertThat(response.userId()).isEqualTo(userId);

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(responsibilityRepository).save(any(Responsibility.class));
    }

    @Test
    void create_WhenTaskNotFound_ShouldThrowException() {
        var request = new ResponsibilityCreateRequest(taskId, userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> responsibilityService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");

        verify(userRepository, never()).findById(any());
        verify(responsibilityRepository, never()).save(any());
    }

    @Test
    void create_WhenUserNotFound_ShouldThrowException() {
        var request = new ResponsibilityCreateRequest(taskId, userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> responsibilityService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(responsibilityRepository, never()).save(any());
    }

    @Test
    void getById_WhenExists_ShouldReturnResponse() {
        when(responsibilityRepository.findById(responsibilityId)).thenReturn(Optional.of(responsibility));

        ResponsibilityResponse response = responsibilityService.getById(responsibilityId);

        assertThat(response.id()).isEqualTo(responsibilityId);
        assertThat(response.taskId()).isEqualTo(taskId);
        assertThat(response.userId()).isEqualTo(userId);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(responsibilityRepository.findById(responsibilityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> responsibilityService.getById(responsibilityId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Responsibility not found");
    }

    @Test
    void getAll_ShouldReturnListOfResponses() {
        var anotherResp = Responsibility.builder()
                .id(UUID.randomUUID())
                .task(Task.builder().id(UUID.randomUUID()).build())
                .user(User.builder().id(UUID.randomUUID()).build())
                .build();

        when(responsibilityRepository.findAll()).thenReturn(List.of(responsibility, anotherResp));

        List<ResponsibilityResponse> responses = responsibilityService.getAll();

        assertThat(responses).hasSize(2);
        verify(responsibilityRepository).findAll();
    }

    @Test
    void getAllByTaskId_ShouldReturnFilteredResponses() {
        when(responsibilityRepository.getByTaskId(taskId)).thenReturn(List.of(responsibility));

        List<ResponsibilityResponse> responses = responsibilityService.getAllByTaskId(taskId);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().taskId()).isEqualTo(taskId);
        verify(responsibilityRepository).getByTaskId(taskId);
    }

    @Test
    void getAllByUserId_ShouldReturnFilteredResponses() {
        when(responsibilityRepository.getByUserId(userId)).thenReturn(List.of(responsibility));

        List<ResponsibilityResponse> responses = responsibilityService.getAllByUserId(userId);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().userId()).isEqualTo(userId);
        verify(responsibilityRepository).getByUserId(userId);
    }

    @Test
    void updateById_WithFullData_ShouldUpdateBothFields() {
        UUID newTaskId = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();
        Task newTask = Task.builder().id(newTaskId).build();
        User newUser = User.builder().id(newUserId).build();

        var request = new ResponsibilityUpdateRequest(newTaskId, newUserId);

        when(responsibilityRepository.findById(responsibilityId)).thenReturn(Optional.of(responsibility));
        when(taskRepository.findById(newTaskId)).thenReturn(Optional.of(newTask));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(responsibilityRepository.save(any(Responsibility.class))).thenReturn(responsibility);

        ResponsibilityResponse response = responsibilityService.updateById(responsibilityId, request);

        assertThat(response.taskId()).isEqualTo(newTaskId);
        assertThat(response.userId()).isEqualTo(newUserId);
        verify(taskRepository).findById(newTaskId);
        verify(userRepository).findById(newUserId);
    }

    @Test
    void updateById_WithPartialData_ShouldUpdateOnlyNonNullFields() {
        UUID newTaskId = UUID.randomUUID();
        Task newTask = Task.builder().id(newTaskId).build();
        var request = new ResponsibilityUpdateRequest(newTaskId, null);

        when(responsibilityRepository.findById(responsibilityId)).thenReturn(Optional.of(responsibility));
        when(taskRepository.findById(newTaskId)).thenReturn(Optional.of(newTask));
        when(responsibilityRepository.save(any(Responsibility.class))).thenReturn(responsibility);

        responsibilityService.updateById(responsibilityId, request);

        verify(taskRepository).findById(newTaskId);
        verify(userRepository, never()).findById(any());
        verify(responsibilityRepository).save(any());
    }

    @Test
    void updateById_WhenResponsibilityNotFound_ShouldThrowException() {
        var request = new ResponsibilityUpdateRequest(null, null);
        when(responsibilityRepository.findById(responsibilityId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> responsibilityService.updateById(responsibilityId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Responsibility not found with id: " + responsibilityId);

        verify(taskRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
        verify(responsibilityRepository, never()).save(any());
    }

    @Test
    void updateById_WhenNewTaskNotFound_ShouldThrowException() {
        UUID newTaskId = UUID.randomUUID();
        var request = new ResponsibilityUpdateRequest(newTaskId, null);

        when(responsibilityRepository.findById(responsibilityId)).thenReturn(Optional.of(responsibility));
        when(taskRepository.findById(newTaskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> responsibilityService.updateById(responsibilityId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");

        verify(responsibilityRepository, never()).save(any());
    }

    @Test
    void deleteById_ShouldCallRepositoryAndReturnTrue() {
        doNothing().when(responsibilityRepository).deleteById(responsibilityId);

        Boolean result = responsibilityService.deleteById(responsibilityId);

        assertThat(result).isTrue();
        verify(responsibilityRepository).deleteById(responsibilityId);
    }
}
