package ru.denisov.itcompany.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.denisov.core.entities.Project;
import ru.denisov.core.entities.User;
import ru.denisov.itcompany.dto.UserCreateRequest;
import ru.denisov.itcompany.dto.UserResponse;
import ru.denisov.itcompany.dto.UserUpdateRequest;
import ru.denisov.itcompany.repository.ProjectRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private UUID projectId;
    private Project project;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        Instant birthDate = Instant.parse("1990-01-01T00:00:00Z");

        project = Project.builder().id(projectId).build();
        user = User.builder()
                .id(userId)
                .name("Ivan")
                .surname("Ivanov")
                .patronymic("Ivanovich")
                .birthDate(birthDate)
                .email("ivan@example.com")
                .password("secret")
                .project(project)
                .build();
    }

    // ==================== CREATE ====================

    @Test
    void create_ShouldFindProject_SaveAndReturnResponse() {
        Instant newBirthDate = Instant.parse("1995-05-05T00:00:00Z");
        var request = new UserCreateRequest(
                "Petr", "Petrov", "Petrovich",
                newBirthDate,
                "petr@example.com", "pass123",
                projectId // Передаем projectId из DTO
        );

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .name("Petr").surname("Petrov").patronymic("Petrovich")
                .birthDate(newBirthDate)
                .email("petr@example.com").password("pass123")
                .project(project)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.create(request);

        assertThat(response.name()).isEqualTo("Petr");
        assertThat(response.birthDate()).isEqualTo(newBirthDate);
        assertThat(response.projectId()).isEqualTo(projectId);

        verify(projectRepository).findById(projectId);
        verify(userRepository).save(argThat(u ->
                u.getName().equals("Petr") && u.getProject().equals(project)
        ));
    }

    @Test
    void create_WhenProjectNotFound_ShouldThrowException() {
        UUID nonExistentProjectId = UUID.randomUUID();
        var request = new UserCreateRequest(
                "Petr", "Petrov", "Petrovich",
                Instant.now(), "petr@example.com", "pass123",
                nonExistentProjectId
        );

        when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");

        verify(userRepository, never()).save(any());
    }

    // ==================== GET BY ID ====================

    @Test
    void getById_WhenExists_ShouldReturnResponse() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse response = userService.getById(userId);

        assertThat(response.id()).isEqualTo(userId);
        assertThat(response.name()).isEqualTo("Ivan");
        assertThat(response.projectId()).isEqualTo(projectId);
    }

    @Test
    void getById_WhenNotExists_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id: " + userId);
    }

    // ==================== GET ALL / BY PROJECT ====================

    @Test
    void getAll_ShouldReturnListOfResponses() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> responses = userService.getAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().id()).isEqualTo(userId);
    }

    @Test
    void getAllByProjectId_ShouldReturnFilteredResponses() {
        when(userRepository.getByProjectId(projectId)).thenReturn(List.of(user));

        List<UserResponse> responses = userService.getAllByProjectId(projectId);

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().projectId()).isEqualTo(projectId);
        verify(userRepository).getByProjectId(projectId);
    }

    // ==================== UPDATE ====================

    @Test
    void updateById_WithFullData_ShouldUpdateAllFields() {
        UUID newProjectId = UUID.randomUUID();
        Project newProject = Project.builder().id(newProjectId).build();
        Instant newBirthDate = Instant.parse("2000-12-31T23:59:59Z");

        var request = new UserUpdateRequest(
                "NewName", "NewSurname", "NewPatronymic",
                newBirthDate, newProjectId
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.of(newProject));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateById(userId, request);

        assertThat(response.name()).isEqualTo("NewName");
        assertThat(response.surname()).isEqualTo("NewSurname");
        assertThat(response.patronymic()).isEqualTo("NewPatronymic");
        assertThat(response.birthDate()).isEqualTo(newBirthDate);
        assertThat(response.projectId()).isEqualTo(newProjectId);

        verify(projectRepository).findById(newProjectId);
        verify(userRepository).save(any());
    }

    @Test
    void updateById_WithNameOnly_ShouldNotTouchOtherFields() {
        var request = new UserUpdateRequest("OnlyName", null, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateById(userId, request);

        assertThat(user.getName()).isEqualTo("OnlyName");
        assertThat(user.getSurname()).isEqualTo("Ivanov"); // Не изменилось
        verify(projectRepository, never()).findById(any());
        verify(userRepository).save(any());
    }

    @Test
    void updateById_WithProjectOnly_ShouldValidateProject() {
        UUID newProjectId = UUID.randomUUID();
        Project newProject = Project.builder().id(newProjectId).build();
        var request = new UserUpdateRequest(null, null, null, null, newProjectId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.of(newProject));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateById(userId, request);

        verify(projectRepository).findById(newProjectId);
        verify(userRepository).save(any());
    }

    @Test
    void updateById_WhenUserNotFound_ShouldThrowException() {
        var request = new UserUpdateRequest("Name", null, null, null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateById(userId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, never()).save(any());
        verify(projectRepository, never()).findById(any());
    }

    @Test
    void updateById_WhenNewProjectNotFound_ShouldThrowException() {
        UUID newProjectId = UUID.randomUUID();
        var request = new UserUpdateRequest(null, null, null, null, newProjectId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateById(userId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");

        verify(userRepository, never()).save(any());
    }

    // ==================== DELETE ====================

    @Test
    void deleteById_ShouldCallRepositoryAndReturnTrue() {
        doNothing().when(userRepository).deleteById(userId);

        Boolean result = userService.deleteById(userId);

        assertThat(result).isTrue();
        verify(userRepository).deleteById(userId);
    }
}