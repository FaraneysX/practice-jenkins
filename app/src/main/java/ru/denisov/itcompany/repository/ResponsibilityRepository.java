package ru.denisov.itcompany.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.denisov.core.entities.Responsibility;

import java.util.List;
import java.util.UUID;

public interface ResponsibilityRepository extends JpaRepository<Responsibility, UUID> {
    List<Responsibility> getByTaskId(UUID id);

    List<Responsibility> getByUserId(UUID id);
}
