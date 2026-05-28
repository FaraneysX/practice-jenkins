package ru.denisov.itcompany.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.denisov.core.entities.Project;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
}
