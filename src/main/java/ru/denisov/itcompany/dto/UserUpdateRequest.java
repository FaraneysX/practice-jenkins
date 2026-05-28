package ru.denisov.itcompany.dto;

import java.time.Instant;
import java.util.UUID;

public record UserUpdateRequest(
        String name,
        String surname,
        String patronymic,
        Instant birthDate,
        UUID projectId
) {
}
