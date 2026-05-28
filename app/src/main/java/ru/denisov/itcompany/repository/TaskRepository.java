package ru.denisov.itcompany.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.denisov.core.entities.Task;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> getByProjectId(UUID id);
}
