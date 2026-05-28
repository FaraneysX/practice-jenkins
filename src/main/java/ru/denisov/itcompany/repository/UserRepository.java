package ru.denisov.itcompany.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.denisov.itcompany.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> getByProjectId(UUID id);
}
