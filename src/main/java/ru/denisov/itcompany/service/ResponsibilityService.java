package ru.denisov.itcompany.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denisov.itcompany.dto.ResponsibilityCreateRequest;
import ru.denisov.itcompany.dto.ResponsibilityResponse;
import ru.denisov.itcompany.dto.ResponsibilityUpdateRequest;
import ru.denisov.itcompany.entity.Responsibility;
import ru.denisov.itcompany.repository.ResponsibilityRepository;
import ru.denisov.itcompany.repository.TaskRepository;
import ru.denisov.itcompany.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResponsibilityService {
    private final ResponsibilityRepository responsibilityRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponsibilityResponse create(ResponsibilityCreateRequest request) {
        final var task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + request.taskId()));

        final var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.userId()));

        final var responsibility = Responsibility.builder()
                .task(task)
                .user(user)
                .build();

        final var savedResponsibility = responsibilityRepository.save(responsibility);

        return new ResponsibilityResponse(
                savedResponsibility.getId(),
                savedResponsibility.getTask().getId(),
                savedResponsibility.getUser().getId()
        );
    }

    @Transactional(readOnly = true)
    public ResponsibilityResponse getById(UUID id) {
        final var responsibility = responsibilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Responsibility not found with id: " + id));

        return new ResponsibilityResponse(
                responsibility.getId(),
                responsibility.getTask().getId(),
                responsibility.getUser().getId()
        );
    }

    @Transactional(readOnly = true)
    public List<ResponsibilityResponse> getAll() {
        return responsibilityRepository
                .findAll()
                .stream()
                .map(r -> new ResponsibilityResponse(
                                r.getId(),
                                r.getTask().getId(),
                                r.getUser().getId()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponsibilityResponse> getAllByTaskId(UUID id) {
        return responsibilityRepository
                .getByTaskId(id)
                .stream()
                .map(r -> new ResponsibilityResponse(
                                r.getId(),
                                r.getTask().getId(),
                                r.getUser().getId()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponsibilityResponse> getAllByUserId(UUID id) {
        return responsibilityRepository
                .getByUserId(id)
                .stream()
                .map(r -> new ResponsibilityResponse(
                                r.getId(),
                                r.getTask().getId(),
                                r.getUser().getId()
                        )
                )
                .toList();
    }

    @Transactional
    public ResponsibilityResponse updateById(UUID id, ResponsibilityUpdateRequest request) {
        final var responsibility = responsibilityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Responsibility not found with id: " + id));

        if (request.taskId() != null) {
            final var task = taskRepository.findById(request.taskId())
                    .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + request.taskId()));

            responsibility.setTask(task);
        }

        if (request.userId() != null) {
            final var user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.userId()));

            responsibility.setUser(user);
        }

        final var updatedResponsibility = responsibilityRepository.save(responsibility);

        return new ResponsibilityResponse(
                updatedResponsibility.getId(),
                updatedResponsibility.getTask().getId(),
                updatedResponsibility.getUser().getId()
        );
    }

    @Transactional
    public Boolean deleteById(UUID id) {
        responsibilityRepository.deleteById(id);

        return true;
    }
}
