package ru.denisov.itcompany.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denisov.core.entities.Project;
import ru.denisov.itcompany.dto.ProjectCreateRequest;
import ru.denisov.itcompany.dto.ProjectResponse;
import ru.denisov.itcompany.dto.ProjectUpdateRequest;
import ru.denisov.itcompany.repository.ProjectRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        final var project =
                Project.builder()
                        .name(request.name())
                        .build();

        final var savedProject = projectRepository.save(project);

        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getCreatedAt(),
                savedProject.getEndedAt()
        );
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(UUID id) {
        final var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getCreatedAt(),
                project.getEndedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAll() {
        return projectRepository
                .findAll()
                .stream()
                .map(p -> new ProjectResponse(
                                p.getId(),
                                p.getName(),
                                p.getCreatedAt(),
                                p.getEndedAt()
                        )
                )
                .toList();
    }

    @Transactional
    public ProjectResponse updateById(UUID id, ProjectUpdateRequest request) {
        final var project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));

        if (request.name() != null) {
            project.setName(request.name());
        }

        if (request.startedAt() != null) {
            project.setCreatedAt(request.startedAt());
        }

        if (request.endedAt() != null) {
            project.setEndedAt(request.endedAt());
        }

        final var updatedProject = projectRepository.save(project);

        return new ProjectResponse(
                updatedProject.getId(),
                updatedProject.getName(),
                updatedProject.getCreatedAt(),
                updatedProject.getEndedAt()
        );
    }

    @Transactional
    public Boolean deleteById(UUID id) {
        projectRepository.deleteById(id);

        return true;
    }
}
