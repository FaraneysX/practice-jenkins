package ru.denisov.itcompany.dto;

import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,

        String name,
        Instant startedAt,
        Instant endedAt,

        UUID projectId
) {
}
