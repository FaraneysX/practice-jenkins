package ru.denisov.itcompany.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denisov.itcompany.dto.TaskCreateRequest;
import ru.denisov.itcompany.dto.TaskResponse;
import ru.denisov.itcompany.dto.TaskUpdateRequest;
import ru.denisov.core.entities.Task;
import ru.denisov.itcompany.repository.ProjectRepository;
import ru.denisov.itcompany.repository.TaskRepository;
import ru.denisov.itcompany.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public TaskResponse create(TaskCreateRequest request) {
        final var project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + request.projectId()));

        final var task = Task.builder()
                .name(request.name())
                .project(project)
                .build();

        final var savedTask = taskRepository.save(task);

        return new TaskResponse(
                savedTask.getId(),
                savedTask.getName(),
                savedTask.getCreatedAt(),
                savedTask.getEndedAt(),
                savedTask.getProject().getId()
        );
    }

    @Transactional(readOnly = true)
    public TaskResponse getById(UUID id) {
        final var task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getCreatedAt(),
                task.getEndedAt(),
                task.getProject().getId()
        );
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        return taskRepository
                .findAll()
                .stream()
                .map(t -> new TaskResponse(
                                t.getId(),
                                t.getName(),
                                t.getCreatedAt(),
                                t.getEndedAt(),
                                t.getProject().getId()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllByProjectId(UUID id) {
        return taskRepository
                .getByProjectId(id)
                .stream()
                .map(t -> new TaskResponse(
                                t.getId(),
                                t.getName(),
                                t.getCreatedAt(),
                                t.getEndedAt(),
                                t.getProject().getId()
                        )
                )
                .toList();
    }

    @Transactional
    public TaskResponse updateById(UUID id, TaskUpdateRequest request) {
        final var task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        if (request.name() != null) {
            task.setName(request.name());
        }

        if (request.startedAt() != null) {
            task.setCreatedAt(request.startedAt());
        }

        if (request.endedAt() != null) {
            task.setEndedAt(request.endedAt());
        }

        if (request.projectId() != null) {
            final var project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + request.projectId()));

            task.setProject(project);
        }

        final var updatedTask = taskRepository.save(task);

        return new TaskResponse(
                updatedTask.getId(),
                updatedTask.getName(),
                updatedTask.getCreatedAt(),
                updatedTask.getEndedAt(),
                updatedTask.getProject().getId()
        );
    }

    @Transactional
    public Boolean deleteById(UUID id) {
        taskRepository.deleteById(id);

        return true;
    }
}
