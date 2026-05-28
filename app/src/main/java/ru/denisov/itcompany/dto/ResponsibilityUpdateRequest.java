package ru.denisov.itcompany.dto;

import java.util.UUID;

public record ResponsibilityUpdateRequest(
        UUID taskId,
        UUID userId
) {
}
