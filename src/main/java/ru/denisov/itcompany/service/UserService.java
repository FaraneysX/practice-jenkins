package ru.denisov.itcompany.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.denisov.itcompany.dto.UserCreateRequest;
import ru.denisov.itcompany.dto.UserResponse;
import ru.denisov.itcompany.dto.UserUpdateRequest;
import ru.denisov.itcompany.entity.User;
import ru.denisov.itcompany.repository.ProjectRepository;
import ru.denisov.itcompany.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        final var user =
                User.builder()
                        .name(request.name())
                        .surname(request.surname())
                        .patronymic(request.patronymic())
                        .birthDate(request.birthDate())
                        .email(request.email())
                        .password(request.password())
                        .build();

        final var savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getSurname(),
                savedUser.getPatronymic(),
                savedUser.getBirthDate(),
                savedUser.getEmail(),
                savedUser.getProject().getId()
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        final var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getPatronymic(),
                user.getBirthDate(),
                user.getEmail(),
                user.getProject().getId()
        );
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository
                .findAll()
                .stream()
                .map(u -> new UserResponse(
                                u.getId(),
                                u.getName(),
                                u.getSurname(),
                                u.getPatronymic(),
                                u.getBirthDate(),
                                u.getEmail(),
                                u.getProject().getId()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllByProjectId(UUID id) {
        return userRepository
                .getByProjectId(id)
                .stream()
                .map(u -> new UserResponse(
                                u.getId(),
                                u.getName(),
                                u.getSurname(),
                                u.getPatronymic(),
                                u.getBirthDate(),
                                u.getEmail(),
                                u.getProject().getId()
                        )
                )
                .toList();
    }

    @Transactional
    public UserResponse updateById(UUID id, UserUpdateRequest request) {
        final var user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (request.name() != null) {
            user.setName(request.name());
        }

        if (request.surname() != null) {
            user.setSurname(request.surname());
        }

        if (request.patronymic() != null) {
            user.setPatronymic(request.patronymic());
        }

        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }

        if (request.projectId() != null) {
            final var project = projectRepository.findById(request.projectId())
                    .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + request.projectId()));

            user.setProject(project);
        }

        final var updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getSurname(),
                updatedUser.getPatronymic(),
                updatedUser.getBirthDate(),
                updatedUser.getEmail(),
                updatedUser.getProject().getId()
        );
    }

    @Transactional
    public Boolean deleteById(UUID id) {
        userRepository.deleteById(id);

        return true;
    }
}
