package ru.denisov.itcompany.dto;

import java.util.UUID;

public record ResponsibilityResponse(
        UUID id,
        UUID taskId,
        UUID userId
) {
}
